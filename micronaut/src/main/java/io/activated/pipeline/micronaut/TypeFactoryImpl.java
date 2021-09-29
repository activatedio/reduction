package io.activated.pipeline.micronaut;

import com.google.common.collect.Sets;
import graphql.Scalars;
import graphql.schema.*;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TypeFactoryImpl implements TypeFactory {

  private final TypeCache<GraphQLInputType> inputCache;
  private final TypeCache<GraphQLOutputType> outputCache;
  private final TypeCache<GraphQLOutputType> outputListCache;

  private static final Set<String> EXCLUDED_PROPERTIES = Sets.newHashSet("class", "declaringClass");

  @Inject
  public TypeFactoryImpl(
      final TypeCache<GraphQLInputType> inputCache,
      final TypeCache<GraphQLOutputType> outputCache,
      final TypeCache<GraphQLOutputType> outputListCache) {
    this.inputCache = inputCache;
    this.outputCache = outputCache;
    this.outputListCache = outputListCache;
    initTypes();
  }

  private void initTypes() {

    outputCache.put(String.class, Scalars.GraphQLString);
    outputCache.put(Integer.class, Scalars.GraphQLInt);
    outputCache.put(int.class, Scalars.GraphQLInt);
    outputCache.put(Boolean.class, Scalars.GraphQLBoolean);
    outputCache.put(boolean.class, Scalars.GraphQLBoolean);
    outputCache.put(Float.class, Scalars.GraphQLFloat);
    outputCache.put(float.class, Scalars.GraphQLFloat);
    outputCache.put(Double.class, Scalars.GraphQLFloat);
    outputCache.put(double.class, Scalars.GraphQLFloat);
    outputCache.put(BigDecimal.class, Scalars.GraphQLBigDecimal);

    inputCache.put(String.class, Scalars.GraphQLString);
    inputCache.put(Integer.class, Scalars.GraphQLInt);
    inputCache.put(int.class, Scalars.GraphQLInt);
    inputCache.put(Boolean.class, Scalars.GraphQLBoolean);
    inputCache.put(boolean.class, Scalars.GraphQLBoolean);
    inputCache.put(Float.class, Scalars.GraphQLFloat);
    inputCache.put(float.class, Scalars.GraphQLFloat);
    inputCache.put(Double.class, Scalars.GraphQLFloat);
    inputCache.put(double.class, Scalars.GraphQLFloat);
    inputCache.put(BigDecimal.class, Scalars.GraphQLBigDecimal);

  }

  @Override
  public GraphQLOutputType getOutputType(final Class<?> input) {

    final var type = outputCache.get(input);

    if (type == null) {
      return makeOutputObjectType(input);
    } else {
      return type;
    }
  }

  private GraphQLOutputType getOutputListType(final Class<?> element) {

    final var type = outputListCache.get(element);

    if (type == null) {
      return makeOutputObjectListType(element);
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

  private GraphQLOutputType makeOutputObjectType(final Class<?> input) {

    try {

      final var pDescs = Introspector.getBeanInfo(input).getPropertyDescriptors();
      var oType = GraphQLObjectType.newObject().name(input.getSimpleName());

      for (final var pDesc : pDescs) {
        if (EXCLUDED_PROPERTIES.contains(pDesc.getName())) {
          continue;
        }
        GraphQLOutputType fType;
        if (pDesc.getPropertyType().equals(List.class)) {
          var elType = ((ParameterizedType) pDesc.getReadMethod().getGenericReturnType()).getActualTypeArguments()[0];
          fType = getOutputListType((Class<?>) elType);
        } else {
          fType = getOutputType(pDesc.getPropertyType());
        }
        oType =
            oType.field(
                field -> field.name(pDesc.getName()).type(fType));
      }

      final var result = oType.build();
      outputCache.put(input, result);
      return result;

    } catch (final IntrospectionException e) {
      throw new RuntimeException(e);
    }
  }

  private GraphQLOutputType makeOutputObjectListType(final Class<?> input) {
      return new GraphQLList(getOutputType(input));
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
