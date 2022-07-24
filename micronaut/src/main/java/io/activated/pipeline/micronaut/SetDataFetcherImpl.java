package io.activated.pipeline.micronaut;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQLContext;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import io.activated.pipeline.micronaut.internal.Constants;
import io.micronaut.http.HttpRequest;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
public class SetDataFetcherImpl<S, A> implements DataFetcher<CompletableFuture<SetResult<S>>> {

  // TODO - think we can make this a shared instance
  private final ObjectMapper mapper = new ObjectMapper();

  private final ContextFactory contextFactory;
  private final Pipeline pipeline;
  private final Class<S> stateClass;
  private final Class<A> actionClass;

  @Inject
  public SetDataFetcherImpl(
      ContextFactory contextFactory,
      final Pipeline pipeline,
      final Class<S> stateClass,
      final Class<A> actionClass) {
    this.contextFactory = contextFactory;
    this.pipeline = pipeline;
    this.stateClass = stateClass;
    this.actionClass = actionClass;
  }

  @Override
  public CompletableFuture<SetResult<S>> get(final DataFetchingEnvironment environment)
      throws Exception {
    var _ctx = (GraphQLContext) environment.getContext();
    final var arg = environment.getArgument("action");
    final var action = mapper.convertValue(arg, actionClass);
    final var ctxFactory =
        contextFactory.create(
            Objects.requireNonNull(
                (HttpRequest) _ctx.get(Constants.GRAPHQL_CONTEXT_REQUEST_KEY), "request"));
    return ctxFactory.flatMap(ctx -> Mono.from(pipeline.set(ctx, stateClass, action))).toFuture();
  }
}
