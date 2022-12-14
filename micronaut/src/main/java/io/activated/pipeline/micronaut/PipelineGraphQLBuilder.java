package io.activated.pipeline.micronaut;

import com.google.common.collect.Maps;
import graphql.schema.*;
import io.activated.pipeline.internal.*;
import io.activated.pipeline.micronaut.internal.MapTypeCache;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PipelineGraphQLBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(PipelineGraphQLBuilder.class);

  private static final String QUERY_ROOT = "Query";
  private static final String MUTATION_ROOT = "Mutation";

  private final Registry pipelineRegistry;
  private final DataFetcherFactory dataFetcherFactory;

  private final List<TypeCacheBuilder> typeCacheBuilders;

  @Inject
  public PipelineGraphQLBuilder(
      final Registry pipelineRegistry,
      final DataFetcherFactory dataFetcherFactory,
      List<TypeCacheBuilder> typeCacheBuilders) {
    this.pipelineRegistry = pipelineRegistry;
    this.dataFetcherFactory = dataFetcherFactory;
    this.typeCacheBuilders = typeCacheBuilders;
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
            new MapTypeCache<>(),
            new MapTypeCache<>(),
            new MapTypeCache<>(),
            new MapTypeCache<>(),
            typeCacheBuilders);

    var stateTypes = pipelineRegistry.getStateTypes();

    Map<String, GraphQLOutputType> stateOutputTypes = Maps.newHashMap();

    for (final var state : stateTypes) {

      var exported = state;

      var isExportable = TypeUtils.isExportable(state);
      if (isExportable) {
        exported = TypeUtils.toExportable(state);
      }

      final var oType = typeFactory.getOutputType(exported);
      final var queryName = lowerCamelCase(exported.getSimpleName());
      var stateOutputType = makeOutputType(exported.getSimpleName(), oType);
      stateOutputTypes.put(exported.getSimpleName(), stateOutputType);
      qObj = qObj.field(field -> field.name(queryName).type(stateOutputType));
      final var dFetch =
          isExportable
              ? dataFetcherFactory.getExportableGetDataFetcher(state)
              : dataFetcherFactory.getGetDataFetcher(state);
      registry = registry.dataFetcher(FieldCoordinates.coordinates(QUERY_ROOT, queryName), dFetch);
    }

    var reducerKeys = pipelineRegistry.getReducerKeys();

    for (final var reducerKey : reducerKeys) {

      // TODO - Scan these interfaces for the candidate - in case there are more than one
      final var stateClass = (Class<?>) reducerKey.getStateType();

      var exportedStateClass = stateClass;

      var isExportable = TypeUtils.isExportable(stateClass);
      if (isExportable) {
        exportedStateClass = TypeUtils.toExportable(stateClass);
      }

      final var actionClass = (Class<?>) reducerKey.getActionType();
      final var iType = typeFactory.getInputType(actionClass);
      final var stateOutputType = stateOutputTypes.get(exportedStateClass.getSimpleName());
      final var mutationName =
          lowerCamelCase(exportedStateClass.getSimpleName() + actionClass.getSimpleName());
      mObj =
          mObj.field(
              field ->
                  field
                      .name(mutationName)
                      .argument(a -> a.name("action").type(iType))
                      .type(stateOutputType));
      final var dFetch =
          isExportable
              ? dataFetcherFactory.getExportableSetDataFetcher(stateClass, actionClass)
              : dataFetcherFactory.getSetDataFetcher(stateClass, actionClass);
      registry =
          registry.dataFetcher(FieldCoordinates.coordinates(MUTATION_ROOT, mutationName), dFetch);
    }
  }

  private static GraphQLOutputType makeOutputType(
      final String name, final GraphQLOutputType oType) {
    return GraphQLObjectType.newObject()
        .name(name + "State")
        .field(f -> f.name("state").type(oType))
        .build();
  }

  private static String lowerCamelCase(final String input) {
    final char[] c = input.toCharArray();
    c[0] = Character.toLowerCase(c[0]);
    return new String(c);
  }
}
