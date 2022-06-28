package io.activated.pipeline.micronaut;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Context;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import reactor.core.publisher.Mono;

@Singleton
public class SetDataFetcherImpl<S, A> implements DataFetcher<CompletableFuture<SetResult<S>>> {

  private final ObjectMapper mapper = new ObjectMapper();

  private final ContextFactory contextFactory;
  private final Pipeline pipeline;
  private final Class<S> stateClass;
  private final Class<A> actionClass;

  @Inject
  public SetDataFetcherImpl(
      ContextFactory contextFactory, final Pipeline pipeline, final Class<S> stateClass, final Class<A> actionClass) {
    this.contextFactory = contextFactory;
    this.pipeline = pipeline;
    this.stateClass = stateClass;
    this.actionClass = actionClass;
  }

  @Override
  public CompletableFuture<SetResult<S>> get(final DataFetchingEnvironment environment)
      throws Exception {
    final var arg = environment.getArgument("action");
    final var action = mapper.convertValue(arg, actionClass);
    return contextFactory.create().flatMap(c -> pipeline.set(c, stateClass, action)).toFuture();
  }
}
