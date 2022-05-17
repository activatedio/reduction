package io.activated.pipeline.micronaut;

import io.activated.objectdiff.SnapshotterImpl;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.StateAccess;
import io.activated.pipeline.builtin.security.SecurityStateGuard;
import io.activated.pipeline.env.PrincipalSupplier;
import io.activated.pipeline.internal.*;
import io.activated.pipeline.repository.RedisStateRepository;
import io.activated.pipeline.repository.StateRepository;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
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
  public StatefulRedisConnection<?, ?> redisClient(MicronautPipelineConfiguration config) {

    var host = config.getRedisHost();
    var port = config.getRedisPort();

    LOGGER.info("Using pipeline.redisHost: [{}] pipeline.redisPort: [{}]", host, port);

    return RedisClient.create(String.format("redis://%s:%d", host, port)).connect();
  }

  @Singleton
  @Inject
  public StateRepository stateRepository(
      MicronautPipelineConfiguration config, StatefulRedisConnection<String, String> connection) {
    return new RedisStateRepository(connection, config);
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
      Registry registry,
      StateAccess stateAccess,
      StateRepository stateRepository,
      ChangeLogger changeLogger) {
    return new ValidatingPipeline(
        validatorFactory,
        new PipelineImpl(
            registry, stateAccess, stateRepository, new SnapshotterImpl(), changeLogger));
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
