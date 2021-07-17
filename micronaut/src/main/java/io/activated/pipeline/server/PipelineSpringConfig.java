package io.activated.pipeline.server;

import graphql.schema.*;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.PipelineConfig;
import io.activated.pipeline.StateAccess;
import io.activated.pipeline.builtin.security.SecurityStateGuard;
import io.activated.pipeline.env.PrincipalSupplier;
import io.activated.pipeline.env.SessionIdSupplier;
import io.activated.pipeline.internal.*;
import io.activated.pipeline.key.KeyStrategy;
import io.activated.pipeline.key.SessionKeyStrategy;
import io.activated.pipeline.repository.RedisStateRepository;
import io.activated.pipeline.repository.StateRepository;
import io.activated.pipeline.server.internal.MapTypeCache;
import io.activated.pipeline.server.internal.SessionIdFilter;
import io.lettuce.core.RedisClient;
import java.util.Optional;
import java.util.function.Supplier;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PipelineSpringConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(PipelineSpringConfig.class);

  private static final String QUERY_ROOT = "Query";
  private static final String MUTATION_ROOT = "Mutation";

  @Bean
  public FilterRegistrationBean sessionIdFilter() {
    final FilterRegistrationBean reg = new FilterRegistrationBean(new SessionIdFilter());
    reg.addUrlPatterns("/*");
    reg.setOrder(1);
    return reg;
  }

  @Bean
  @Autowired
  static GraphQLSchema pipelineSchema(
      final Registry pipelineRegistry,
      final TypeFactory typeFactory,
      final DataFetcherFactory dataFetcherFactory,
      final Optional<Supplier<GraphQLSchema>> existingSchemaSupplier) {

    GraphQLCodeRegistry existingRegistry = null;
    GraphQLObjectType existingQObj = null;
    GraphQLObjectType existingMObj = null;

    GraphQLSchema existingSchema = null;

    if (existingSchemaSupplier.isPresent()) {
      existingSchema = existingSchemaSupplier.get().get();
      existingRegistry = existingSchema.getCodeRegistry();
      existingQObj = existingSchema.getQueryType();
      existingMObj = existingSchema.getMutationType();
    }

    var registry =
        existingRegistry != null
            ? GraphQLCodeRegistry.newCodeRegistry(existingRegistry)
            : GraphQLCodeRegistry.newCodeRegistry();
    var qObj =
        existingQObj != null
            ? GraphQLObjectType.newObject(existingQObj)
            : GraphQLObjectType.newObject().name(QUERY_ROOT);
    var mObj =
        existingMObj != null
            ? GraphQLObjectType.newObject(existingMObj)
            : GraphQLObjectType.newObject().name(MUTATION_ROOT);

    for (final var state : pipelineRegistry.getStateTypes()) {
      final var oType = typeFactory.getOutputType(state);
      final var queryName = lowerCamelCase(state.getSimpleName());
      qObj =
          qObj.field(
              field -> field.name(queryName).type(makeGetType(state.getSimpleName(), oType)));
      final var dFetch = dataFetcherFactory.getGetDataFetcher(state);
      registry = registry.dataFetcher(FieldCoordinates.coordinates(QUERY_ROOT, queryName), dFetch);
    }

    for (final var reducerKey : pipelineRegistry.getReducerKeys()) {

      // TODO - Scan these interfaces for the candidate - in case there are more than one
      final var stateClass = (Class<?>) reducerKey.getStateType();
      final var actionClass = (Class<?>) reducerKey.getActionType();
      final var oType = typeFactory.getOutputType(stateClass);
      final var iType = typeFactory.getInputType(actionClass);
      final var mutationName =
          lowerCamelCase(stateClass.getSimpleName() + actionClass.getSimpleName());
      mObj =
          mObj.field(
              field ->
                  field
                      .name(mutationName)
                      .argument(a -> a.name("action").type(iType))
                      .type(
                          makeSetType(
                              stateClass.getSimpleName(), actionClass.getSimpleName(), oType)));
      final var dFetch = dataFetcherFactory.getSetDataFetcher(stateClass, actionClass);
      registry =
          registry.dataFetcher(FieldCoordinates.coordinates(MUTATION_ROOT, mutationName), dFetch);
    }

    if (existingSchema == null) {
      var builder = GraphQLSchema.newSchema();
      builder = builder.query(qObj);
      builder = builder.mutation(mObj);
      builder.codeRegistry(registry.build());
      return builder.build();
    } else {
      var builder = GraphQLSchema.newSchema(existingSchema);
      builder.query(qObj.build());
      builder.mutation(mObj.build());
      var r = registry.build();
      builder.codeRegistry(r);
      return builder.build();
    }
  }

  private static GraphQLOutputType makeGetType(final String name, final GraphQLOutputType oType) {
    return GraphQLObjectType.newObject()
        .name("get" + name)
        .field(f -> f.name("state").type(oType))
        .build();
  }

  private static GraphQLOutputType makeSetType(
      final String stateName, final String actionName, final GraphQLOutputType oType) {
    return GraphQLObjectType.newObject()
        .name("set" + stateName + actionName)
        .field(f -> f.name("state").type(oType))
        .build();
  }

  private static String lowerCamelCase(final String input) {
    final char[] c = input.toCharArray();
    c[0] = Character.toLowerCase(c[0]);
    return new String(c);
  }

  @Bean
  @Autowired
  KeyStrategy keyStrategy(SessionIdSupplier sessionIdSupplier) {
    return new SessionKeyStrategy(sessionIdSupplier);
  }

  @Bean
  TypeFactory typeFactory() {
    return new TypeFactoryImpl(
        new MapTypeCache<GraphQLInputType>(), new MapTypeCache<GraphQLOutputType>());
  }

  @Bean
  @Autowired
  StateRepository stateRepository(MainRuntimeConfiguration configuration) {

    var host = configuration.getRedisHost();
    var port = configuration.getRedisPort();

    LOGGER.info("Using pipeline.redisHost: [{}] pipeline.redisPort: [{}]", host, port);

    RedisClient client = RedisClient.create(String.format("redis://%s:%d", host, port));

    var config = new PipelineConfig();

    return new RedisStateRepository(client.connect(), config);
  }

  @Bean
  @Autowired
  StateAccess stateAccess(
      Registry registry, StateRepository stateRepository, ChangeLogger changeLogger) {
    return new GuardedStateAccess(
        registry,
        new StateAccessImpl(registry, stateRepository, new SnapshotterImpl(), changeLogger));
  }

  @Bean
  @Autowired
  Pipeline pipeline(
      Registry registry,
      StateAccess stateAccess,
      StateRepository stateRepository,
      ChangeLogger changeLogger) {
    return new PipelineImpl(
        registry, stateAccess, stateRepository, new SnapshotterImpl(), changeLogger);
  }

  @Bean
  @Autowired
  ChangeLogger changeLogger(ChangeLoggerRuntimeConfiguration configuration)
      throws PulsarClientException {

    if ("pulsar".equals(configuration.getType())) {
      var client = PulsarClient.builder().serviceUrl(configuration.getPulsarServiceUrl()).build();
      Producer<String> producer =
          client.newProducer(Schema.STRING).topic("authkit-saas-pipeline").create();
      return new PulsarChangeLogger(producer);
    }

    return new Slf4JChangeLogger();
  }

  @Bean
  @Autowired
  SecurityStateGuard securityStateGuard(PrincipalSupplier principalSupplier) {
    return new SecurityStateGuard(principalSupplier);
  }
}
