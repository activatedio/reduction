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
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.schema.GraphQLSchema;
import io.activated.pipeline.micronaut.PipelineGraphQLBuilder;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.core.io.ResourceResolver;
import javax.inject.Singleton;

/** @author Marcel Overdijk */
@Factory
@SuppressWarnings("Duplicates")
public class GraphQLFactory {

  @Bean
  @Singleton
  public GraphQLSchema graphQLSchema(PipelineGraphQLBuilder pipelineGraphQLBuilder) {
    return pipelineGraphQLBuilder.create();
  }
  @Bean
  @Singleton
  public GraphQL graphQL(
      GraphQLSchema graphQLSchema,
      ResourceResolver resourceResolver,
      ToDosDataFetcher toDosDataFetcher,
      CreateToDoDataFetcher createToDoDataFetcher,
      CompleteToDoDataFetcher completeToDoDataFetcher,
      DeleteToDoDataFetcher deleteToDoDataFetcher,
      AuthorDataFetcher authorDataFetcher) {
    /*
            SchemaParser schemaParser = new SchemaParser();
            SchemaGenerator schemaGenerator = new SchemaGenerator();

            // Parse the schema.
            TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();
            typeRegistry.merge(schemaParser.parse(new BufferedReader(new InputStreamReader(
                    resourceResolver.getResourceAsStream("classpath:schema.graphqls").get()))));

            // Create the runtime wiring.
            RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                    .type("Query", typeWiring -> typeWiring
                            .dataFetcher("toDos", toDosDataFetcher))
                    .type("Mutation", typeWiring -> typeWiring
                            .dataFetcher("createToDo", createToDoDataFetcher)
                            .dataFetcher("completeToDo", completeToDoDataFetcher)
                            .dataFetcher("deleteToDo", deleteToDoDataFetcher))
                    .type("ToDo", typeWiring -> typeWiring
                            .dataFetcher("author", authorDataFetcher))
                    .build();

            // Create the executable schema.
            GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);

            // Decorate it with the pipeline
            graphQLSchema = pipelineGraphQLBuilder.build(graphQLSchema);
    */
    // Return the GraphQL bean.
    return GraphQL.newGraphQL(graphQLSchema).mutationExecutionStrategy(new AsyncExecutionStrategy()).build();
  }
}
