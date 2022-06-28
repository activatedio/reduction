package io.activated.pipeline.micronaut;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Exportable;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;

import java.util.concurrent.CompletableFuture;

public class ExportableGetDataFetcherImpl<I extends Exportable<E>, E>
    implements DataFetcher<CompletableFuture<GetResult<E>>> {

  private final ContextFactory contextFactory;
  private final Pipeline pipeline;
  private final Class<I> stateClass;

  public ExportableGetDataFetcherImpl(ContextFactory contextFactory, Pipeline pipeline, Class<I> stateClass) {
    this.contextFactory = contextFactory;
    this.pipeline = pipeline;
    this.stateClass = stateClass;
  }

  @Override
  public CompletableFuture<GetResult<E>> get(final DataFetchingEnvironment environment)
      throws Exception {
    return
        contextFactory.create().flatMap(ctx -> pipeline.get(ctx, stateClass))
        .map(
            gr -> {
              var result = new GetResult<E>();
              result.setState(gr.getState().export());
              return result;
            })
        .toFuture();
  }

}
