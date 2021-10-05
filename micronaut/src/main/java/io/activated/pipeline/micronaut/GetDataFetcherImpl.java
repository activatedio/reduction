package io.activated.pipeline.micronaut;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Constants;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.env.SessionIdSupplier;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

public class GetDataFetcherImpl<S> implements DataFetcher<CompletableFuture<GetResult<S>>> {

  private final Pipeline pipeline;
  private final SessionIdSupplier sessionIdSupplier;
  private final Class<S> stateClass;

  public GetDataFetcherImpl(Pipeline pipeline, SessionIdSupplier sessionIdSupplier, Class<S> stateClass) {
    this.pipeline = pipeline;
    this.sessionIdSupplier = sessionIdSupplier;
    this.stateClass = stateClass;
  }

  @Override
  public CompletableFuture<GetResult<S>> get(final DataFetchingEnvironment environment) throws Exception {
    return Mono.from(pipeline.get(stateClass)).contextWrite(ctx ->
            ctx.put(Constants.SESSION_ID_CONTEXT_KEY, sessionIdSupplier.get())).toFuture();
  }
}
