package io.activated.pipeline.micronaut;

import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

public interface TypeCacheBuilder {

  void build(TypeCache<GraphQLInputType> inputCache, TypeCache<GraphQLOutputType> outputCache);
}
