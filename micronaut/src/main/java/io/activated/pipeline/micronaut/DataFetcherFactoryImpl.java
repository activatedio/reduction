package io.activated.pipeline.micronaut;

import graphql.schema.DataFetcher;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DataFetcherFactoryImpl implements DataFetcherFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataFetcherFactoryImpl.class);

  private final ContextFactory contextFactory;

  private final Pipeline pipeline;

  @Inject
  public DataFetcherFactoryImpl(ContextFactory contextFactory, Pipeline pipeline) {
    this.contextFactory = contextFactory;
    this.pipeline = pipeline;
  }

  @Override
  public <S> DataFetcher<CompletableFuture<GetResult<S>>> getGetDataFetcher(
      final Class<S> stateClass) {

    LOGGER.debug("Creating get DataFetcher for stateClass: {}", stateClass);
    return new GetDataFetcherImpl<S>(contextFactory, pipeline, stateClass);
  }

  @Override
  public <S, A> DataFetcher<CompletableFuture<SetResult<S>>> getSetDataFetcher(
      final Class<S> stateClass, final Class<A> actionClass) {

    LOGGER.debug(
        "Creating set DataFetcher for stateClass: {}, actionClass: {}", stateClass, actionClass);
    return new SetDataFetcherImpl<S, A>(contextFactory, pipeline, stateClass, actionClass);
  }
}
