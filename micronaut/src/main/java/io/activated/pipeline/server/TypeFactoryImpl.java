package io.activated.pipeline.server;

import com.google.common.collect.Sets;
import graphql.Scalars;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.Set;

public class TypeFactoryImpl implements TypeFactory {

  private final TypeCache<GraphQLInputType> inputCache;
  private final TypeCache<GraphQLOutputType> outputCache;

  private static final Set<String> EXCLUDED_PROPERTIES = Sets.newHashSet("class", "declaringClass");

  public TypeFactoryImpl(
      final TypeCache<GraphQLInputType> inputCache,
      final TypeCache<GraphQLOutputType> outputCache) {
    this.inputCache = inputCache;
    this.outputCache = outputCache;
    initTypes();
  }

  private void initTypes() {

    outputCache.put(String.class, Scalars.GraphQLString);
    outputCache.put(Integer.class, Scalars.GraphQLInt);

    inputCache.put(String.class, Scalars.GraphQLString);
    inputCache.put(Integer.class, Scalars.GraphQLInt);
  }

  @Override
  public GraphQLOutputType getOutputType(final Class<?> input) {

    final var type = outputCache.get(input);

    if (type == null) {
      return makeObjectType(input);
    } else {
      return type;
    }
  }

  @Override
  public GraphQLInputType getInputType(final Class<?> input) {

    final var type = inputCache.get(input);

    if (type == null) {
      return makeInputObjectType(input);
    } else {
      return type;
    }
  }

  private GraphQLObjectType makeObjectType(final Class<?> input) {

    try {

      final var pDescs = Introspector.getBeanInfo(input).getPropertyDescriptors();
      var oType = GraphQLObjectType.newObject().name(input.getSimpleName());

      for (final var pDesc : pDescs) {
        if (EXCLUDED_PROPERTIES.contains(pDesc.getName())) {
          continue;
        }
        oType =
            oType.field(
                field -> field.name(pDesc.getName()).type(getOutputType(pDesc.getPropertyType())));
      }

      final var result = oType.build();
      outputCache.put(input, result);
      return result;

    } catch (final IntrospectionException e) {
      throw new RuntimeException(e);
    }
  }

  private GraphQLInputObjectType makeInputObjectType(final Class<?> input) {
    try {

      final var pDescs = Introspector.getBeanInfo(input).getPropertyDescriptors();
      var oType = GraphQLInputObjectType.newInputObject().name(input.getSimpleName() + "Input");

      for (final var pDesc : pDescs) {
        if (EXCLUDED_PROPERTIES.contains(pDesc.getName())) {
          continue;
        }
        oType =
            oType.field(
                field -> field.name(pDesc.getName()).type(getInputType(pDesc.getPropertyType())));
      }

      final var result = oType.build();
      inputCache.put(input, result);
      return result;

    } catch (final IntrospectionException e) {
      throw new RuntimeException(e);
    }
  }
}
