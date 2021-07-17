package io.activated.pipeline.server;

import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

public interface TypeFactory {

  GraphQLOutputType getOutputType(Class<?> input);

  GraphQLInputType getInputType(Class<?> input);
}
