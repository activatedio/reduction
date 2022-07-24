package io.activated.pipeline.micronaut;

import graphql.GraphQLContext;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.micronaut.internal.Constants;
import io.micronaut.http.HttpRequest;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import reactor.core.publisher.Mono;

public class GetDataFetcherImpl<S> implements DataFetcher<CompletableFuture<GetResult<S>>> {

  private final ContextFactory contextFactory;
  private final Pipeline pipeline;
  private final Class<S> stateClass;

  public GetDataFetcherImpl(ContextFactory contextFactory, Pipeline pipeline, Class<S> stateClass) {
    this.contextFactory = contextFactory;
    this.pipeline = pipeline;
    this.stateClass = stateClass;
  }

  @Override
  public CompletableFuture<GetResult<S>> get(final DataFetchingEnvironment environment)
      throws Exception {

    var _ctx = (GraphQLContext) environment.getContext();
    final var ctxFactory =
        contextFactory.create(
            Objects.requireNonNull(
                (HttpRequest) _ctx.get(Constants.GRAPHQL_CONTEXT_REQUEST_KEY), "request"));
    return ctxFactory.flatMap(ctx -> Mono.from(pipeline.get(ctx, stateClass))).toFuture();
  }
}
