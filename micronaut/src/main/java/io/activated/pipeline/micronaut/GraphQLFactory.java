package io.activated.pipeline.micronaut;

import io.activated.objectdiff.SnapshotterImpl;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.PipelineConfig;
import io.activated.pipeline.StateAccess;
import io.activated.pipeline.builtin.security.SecurityStateGuard;
import io.activated.pipeline.env.PrincipalSupplier;
import io.activated.pipeline.internal.*;
import io.activated.pipeline.repository.LockRepository;
import io.activated.pipeline.repository.RedisLockRepository;
import io.activated.pipeline.repository.RedisStateRepository;
import io.activated.pipeline.repository.StateRepository;
import io.lettuce.core.RedisClient;
import io.micronaut.context.annotation.Factory;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ValidatorFactory;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Factory
public class GraphQLFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLFactory.class);

  @Singleton
  @Inject
  public RedisClient redisClient(MainRuntimeConfiguration configuration) {

    var host = configuration.getRedisHost();
    var port = configuration.getRedisPort();

    LOGGER.info("Using pipeline.redisHost: [{}] pipeline.redisPort: [{}]", host, port);

    return RedisClient.create(String.format("redis://%s:%d", host, port));
  }

  @Singleton
  public PipelineConfig pipelineConfig(MainRuntimeConfiguration configuration) {

    var config = new PipelineConfig();

    config.setSessionLockLengthSeconds(configuration.getSessionLockLengthSeconds());
    config.setSessionLockAcquireSeconds(configuration.getSessionLockAcquireSeconds());

    return config;
  }

  @Singleton
  @Inject
  public StateRepository stateRepository(RedisClient client, PipelineConfig pipelineConfig) {

    return new RedisStateRepository(client.connect(), pipelineConfig);
  }

  @Singleton
  @Inject
  public LockRepository lockRepository(RedisClient client, PipelineConfig pipelineConfig) {

    return new RedisLockRepository(client.connect(), pipelineConfig);
  }

  @Singleton
  @Inject
  public StateAccess stateAccess(
      Registry registry, StateRepository stateRepository, ChangeLogger changeLogger) {
    return new GuardedStateAccess(
        registry,
        new StateAccessImpl(registry, stateRepository, new SnapshotterImpl(), changeLogger));
  }

  @Singleton
  @Inject
  public Pipeline pipeline(
      ValidatorFactory validatorFactory,
      LockRepository lockRepository,
      Registry registry,
      StateAccess stateAccess,
      StateRepository stateRepository,
      ChangeLogger changeLogger) {

    return new LockingPipeline(
        lockRepository,
        new ValidatingPipeline(
            validatorFactory,
            new PipelineImpl(
                registry, stateAccess, stateRepository, new SnapshotterImpl(), changeLogger)));
  }

  @Singleton
  @Inject
  public ChangeLogger changeLogger(ChangeLoggerRuntimeConfiguration configuration)
      throws PulsarClientException {

    if ("pulsar".equals(configuration.getType())) {
      var client = PulsarClient.builder().serviceUrl(configuration.getPulsarServiceUrl()).build();
      Producer<String> producer =
          client.newProducer(Schema.STRING).topic("authkit-saas-pipeline").create();
      return new PulsarChangeLogger(producer);
    }

    return new Slf4JChangeLogger();
  }

  @Singleton
  @Inject
  public SecurityStateGuard securityStateGuard(PrincipalSupplier principalSupplier) {
    return new SecurityStateGuard(principalSupplier);
  }
}
