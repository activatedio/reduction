/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.activated.pipeline.micronaut.cart.graphql;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.activated.pipeline.micronaut.PipelineGraphQLBuilder;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
public class GraphQLFactory {

  @Bean
  @Singleton
  public GraphQLSchema graphQLSchema(PipelineGraphQLBuilder pipelineGraphQLBuilder) {
    return pipelineGraphQLBuilder.create();
  }

  @Bean
  @Singleton
  public GraphQL graphQL(GraphQLSchema graphQLSchema) {
    return GraphQL.newGraphQL(graphQLSchema).build();
  }
}
