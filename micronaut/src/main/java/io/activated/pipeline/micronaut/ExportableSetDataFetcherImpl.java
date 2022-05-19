package io.activated.pipeline.micronaut;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Context;
import io.activated.pipeline.Exportable;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;

@Singleton
public class ExportableSetDataFetcherImpl<I extends Exportable<E>, E, A>
    implements DataFetcher<CompletableFuture<SetResult<E>>> {

  private final ObjectMapper mapper = new ObjectMapper();

  private final Pipeline pipeline;
  private final Class<I> stateClass;
  private final Class<A> actionClass;

  @Inject
  public ExportableSetDataFetcherImpl(
      final Pipeline pipeline, final Class<I> stateClass, final Class<A> actionClass) {
    this.pipeline = pipeline;
    this.stateClass = stateClass;
    this.actionClass = actionClass;
  }

  @Override
  public CompletableFuture<SetResult<E>> get(final DataFetchingEnvironment environment)
      throws Exception {
    final var arg = environment.getArgument("action");
    final var action = mapper.convertValue(arg, actionClass);
    return pipeline
        .set(getContext(), stateClass, action)
        .map(
            sr -> {
              var result = new SetResult<E>();
              result.setState(sr.getState().export());
              return result;
            })
        .toFuture();
  }

  protected Context getContext() {
    return ContextUtils.getContext();
  }
}
