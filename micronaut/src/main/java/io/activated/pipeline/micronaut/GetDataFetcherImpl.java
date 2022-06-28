package io.activated.pipeline.micronaut;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import java.util.concurrent.CompletableFuture;

public class GetDataFetcherImpl<S> implements DataFetcher<CompletableFuture<GetResult<S>>> {

  private final Pipeline pipeline;
  private final ContextFactory contextFactory;
  private final Class<S> stateClass;

  public GetDataFetcherImpl(Pipeline pipeline, ContextFactory contextFactory, Class<S> stateClass) {
    this.pipeline = pipeline;
    this.contextFactory = contextFactory;
    this.stateClass = stateClass;
  }

  @Override
  public CompletableFuture<GetResult<S>> get(final DataFetchingEnvironment environment)
      throws Exception {
    return contextFactory.create().flatMap(c -> pipeline.get(c, stateClass)).toFuture();
  }

}
