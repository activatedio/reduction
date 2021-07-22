package io.activated.pipeline.micronaut;

import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

public interface TypeFactory {

  GraphQLOutputType getOutputType(Class<?> input);

  GraphQLInputType getInputType(Class<?> input);
}
