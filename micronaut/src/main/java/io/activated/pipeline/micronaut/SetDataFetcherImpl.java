package io.activated.pipeline.micronaut;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Constants;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import io.activated.pipeline.env.SessionIdSupplier;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

@Singleton
public class SetDataFetcherImpl<S, A> implements DataFetcher<CompletableFuture<SetResult<S>>> {

  private final ObjectMapper mapper = new ObjectMapper();

  private final Pipeline pipeline;
  private final SessionIdSupplier sessionIdSupplier;
  private final Class<S> stateClass;
  private final Class<A> actionClass;

  @Inject
  public SetDataFetcherImpl(
          final Pipeline pipeline, SessionIdSupplier sessionIdSupplier, final Class<S> stateClass, final Class<A> actionClass) {
    this.pipeline = pipeline;
    this.sessionIdSupplier = sessionIdSupplier;
    this.stateClass = stateClass;
    this.actionClass = actionClass;
  }

  @Override
  public CompletableFuture<SetResult<S>> get(final DataFetchingEnvironment environment) throws Exception {
    final var arg = environment.getArgument("action");
    final var action = mapper.convertValue(arg, actionClass);
    var pub = pipeline.set(stateClass, action);
    return Mono.from(pub).contextWrite(ctx -> ctx.put(Constants.SESSION_ID_CONTEXT_KEY,
            sessionIdSupplier.get())).toFuture();
  }
}
