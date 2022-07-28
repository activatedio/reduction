package io.activated.pipeline.micronaut;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import io.activated.pipeline.micronaut.internal.Constants;
import io.micronaut.http.HttpRequest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import reactor.core.publisher.Mono;

@Singleton
public class SetDataFetcherImpl<S, E, A> implements DataFetcher<CompletableFuture<SetResult<E>>> {

  // TODO - think we can make this a shared instance
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final ContextFactory contextFactory;
  private final Pipeline pipeline;
  private final Class<S> stateClass;
  private final Class<A> actionClass;

  private final Function<S, E> mapper;

  @Inject
  public SetDataFetcherImpl(
      ContextFactory contextFactory,
      final Pipeline pipeline,
      final Class<S> stateClass,
      final Class<A> actionClass,
      Function<S, E> mapper) {
    this.contextFactory = contextFactory;
    this.pipeline = pipeline;
    this.stateClass = stateClass;
    this.actionClass = actionClass;
    this.mapper = mapper;
  }

  @Override
  public CompletableFuture<SetResult<E>> get(final DataFetchingEnvironment environment)
      throws Exception {
    var _ctx = environment.getGraphQlContext();
    final var arg = environment.getArgument("action");
    final var action = MAPPER.convertValue(arg, actionClass);
    final var ctxFactory =
        contextFactory.create(
            Objects.requireNonNull(
                (HttpRequest) _ctx.get(Constants.GRAPHQL_CONTEXT_REQUEST_KEY), "request"));
    return ctxFactory
        .flatMap(
            ctx ->
                Mono.from(pipeline.set(ctx, stateClass, action))
                    .map(
                        r -> {
                          var result = new SetResult<E>();
                          result.setState(mapper.apply(r.getState()));
                          return result;
                        }))
        .toFuture();
  }
}
