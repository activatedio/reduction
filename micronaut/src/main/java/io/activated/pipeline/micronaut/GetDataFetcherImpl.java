package io.activated.pipeline.micronaut;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import java.util.concurrent.CompletableFuture;
import reactor.core.publisher.Mono;

public class GetDataFetcherImpl<S> implements DataFetcher<CompletableFuture<GetResult<S>>> {

  private final Pipeline pipeline;
  private final Class<S> stateClass;

  public GetDataFetcherImpl(Pipeline pipeline, Class<S> stateClass) {
    this.pipeline = pipeline;
    this.stateClass = stateClass;
  }

  @Override
  public CompletableFuture<GetResult<S>> get(final DataFetchingEnvironment environment)
      throws Exception {
    return Mono.from(pipeline.get(stateClass)).toFuture();
  }
}
