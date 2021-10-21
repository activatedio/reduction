package io.activated.pipeline.micronaut;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
public class SetDataFetcherImpl<S, A> implements DataFetcher<CompletableFuture<SetResult<S>>> {

  private final ObjectMapper mapper = new ObjectMapper();

  private final Pipeline pipeline;
  private final Class<S> stateClass;
  private final Class<A> actionClass;

  @Inject
  public SetDataFetcherImpl(
      final Pipeline pipeline, final Class<S> stateClass, final Class<A> actionClass) {
    this.pipeline = pipeline;
    this.stateClass = stateClass;
    this.actionClass = actionClass;
  }

  @Override
  public CompletableFuture<SetResult<S>> get(final DataFetchingEnvironment environment)
      throws Exception {
    final var arg = environment.getArgument("action");
    final var action = mapper.convertValue(arg, actionClass);
    final var ctx = ContextUtils.getContext();
    return Mono.from(pipeline.set(ctx, stateClass, action)).toFuture();
  }
}
