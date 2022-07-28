package io.activated.pipeline.micronaut;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.micronaut.internal.Constants;
import io.micronaut.http.HttpRequest;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import reactor.core.publisher.Mono;

public class GetDataFetcherImpl<S, E> implements DataFetcher<CompletableFuture<GetResult<E>>> {

  private final ContextFactory contextFactory;
  private final Pipeline pipeline;
  private final Class<S> stateClass;

  private final Function<S, E> mapper;

  public GetDataFetcherImpl(
      ContextFactory contextFactory,
      Pipeline pipeline,
      Class<S> stateClass,
      Function<S, E> mapper) {
    this.contextFactory = contextFactory;
    this.pipeline = pipeline;
    this.stateClass = stateClass;
    this.mapper = mapper;
  }

  @Override
  public CompletableFuture<GetResult<E>> get(final DataFetchingEnvironment environment)
      throws Exception {

    var _ctx = environment.getGraphQlContext();
    final var ctxFactory =
        contextFactory.create(
            Objects.requireNonNull(
                (HttpRequest) _ctx.get(Constants.GRAPHQL_CONTEXT_REQUEST_KEY), "request"));
    return ctxFactory
        .flatMap(
            ctx ->
                Mono.from(pipeline.get(ctx, stateClass))
                    .map(
                        r -> {
                          var exported = new GetResult<E>();
                          exported.setState(mapper.apply(r.getState()));
                          return exported;
                        }))
        .toFuture();
  }
}
