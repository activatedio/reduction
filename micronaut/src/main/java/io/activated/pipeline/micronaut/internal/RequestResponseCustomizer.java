package io.activated.pipeline.micronaut.internal;

import graphql.ExecutionInput;
import graphql.GraphQLContext;
import io.micronaut.configuration.graphql.GraphQLExecutionInputCustomizer;
import io.micronaut.context.annotation.Primary;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;

@Singleton
@Primary
public class RequestResponseCustomizer implements GraphQLExecutionInputCustomizer {

  @Override
  public Publisher<ExecutionInput> customize(
      ExecutionInput executionInput,
      HttpRequest httpRequest,
      MutableHttpResponse<String> httpResponse) {
    GraphQLContext graphQLContext = (GraphQLContext) executionInput.getContext();
    graphQLContext.put(Constants.GRAPHQL_CONTEXT_REQUEST_KEY, httpRequest);
    return Publishers.just(executionInput);
  }
}
