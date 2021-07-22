package io.activated.pipeline.micronaut;

import graphql.schema.*;
import io.activated.pipeline.internal.*;
import io.activated.pipeline.micronaut.internal.MapTypeCache;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PipelineGraphQLBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(PipelineGraphQLBuilder.class);

  private static final String QUERY_ROOT = "Query";
  private static final String MUTATION_ROOT = "Mutation";

  private final Registry pipelineRegistry;
  private final DataFetcherFactory dataFetcherFactory;

  @Inject
  public PipelineGraphQLBuilder(
      final Registry pipelineRegistry, final DataFetcherFactory dataFetcherFactory) {
    this.pipelineRegistry = pipelineRegistry;
    this.dataFetcherFactory = dataFetcherFactory;
  }

  public GraphQLSchema create() {

    var registry = GraphQLCodeRegistry.newCodeRegistry();
    var qObj = GraphQLObjectType.newObject().name(QUERY_ROOT);
    var mObj = GraphQLObjectType.newObject().name(MUTATION_ROOT);

    doBuild(qObj, mObj, registry);

    var builder = GraphQLSchema.newSchema();
    builder = builder.query(qObj);
    builder = builder.mutation(mObj);
    builder.codeRegistry(registry.build());
    return builder.build();
  }

  public GraphQLSchema build(final GraphQLSchema existingSchema) {

    var registry = GraphQLCodeRegistry.newCodeRegistry(existingSchema.getCodeRegistry());
    var qObj = GraphQLObjectType.newObject(existingSchema.getQueryType());
    var mObj = GraphQLObjectType.newObject(existingSchema.getMutationType());

    var builder = GraphQLSchema.newSchema(existingSchema);
    builder = builder.query(qObj);
    builder = builder.mutation(mObj);
    builder.codeRegistry(registry.build());

    return builder.build();
  }

  private void doBuild(
      GraphQLObjectType.Builder qObj,
      GraphQLObjectType.Builder mObj,
      GraphQLCodeRegistry.Builder registry) {

    var typeFactory =
        new TypeFactoryImpl(
            new MapTypeCache<GraphQLInputType>(), new MapTypeCache<GraphQLOutputType>());

    var stateTypes = pipelineRegistry.getStateTypes();

    for (final var state : stateTypes) {
      final var oType = typeFactory.getOutputType(state);
      final var queryName = lowerCamelCase(state.getSimpleName());
      qObj =
          qObj.field(
              field -> field.name(queryName).type(makeGetType(state.getSimpleName(), oType)));
      final var dFetch = dataFetcherFactory.getGetDataFetcher(state);
      registry = registry.dataFetcher(FieldCoordinates.coordinates(QUERY_ROOT, queryName), dFetch);
    }

    var reducerKeys = pipelineRegistry.getReducerKeys();

    for (final var reducerKey : reducerKeys) {

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
}
