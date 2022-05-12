package io.activated.pipeline.micronaut;

import com.google.common.annotations.VisibleForTesting;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Context;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import java.util.concurrent.CompletableFuture;

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
    return pipeline.get(getContext(), stateClass).toFuture();
  }

  @VisibleForTesting
  protected Context getContext() {
    return ContextUtils.getContext();
  }
}
