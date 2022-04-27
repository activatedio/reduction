package io.activated.pipeline.micronaut;

import graphql.schema.DataFetcher;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DataFetcherFactoryImpl implements DataFetcherFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataFetcherFactoryImpl.class);

  private final Pipeline pipeline;

  @Inject
  public DataFetcherFactoryImpl(Pipeline pipeline) {
    this.pipeline = pipeline;
  }

  @Override
  public <S> DataFetcher<CompletableFuture<GetResult<S>>> getGetDataFetcher(
      final Class<S> stateClass) {

    LOGGER.debug("Creating get DataFetcher for stateClass: {}", stateClass);
    return new GetDataFetcherImpl<S>(pipeline, stateClass);
  }

  @Override
  public <S, A> DataFetcher<CompletableFuture<SetResult<S>>> getSetDataFetcher(
      final Class<S> stateClass, final Class<A> actionClass) {

    LOGGER.debug(
        "Creating set DataFetcher for stateClass: {}, actionClass: {}", stateClass, actionClass);
    return new SetDataFetcherImpl<S, A>(pipeline, stateClass, actionClass);
  }
}
