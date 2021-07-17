package io.activated.pipeline.server;

import graphql.schema.DataFetcher;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataFetcherFactoryImpl implements DataFetcherFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataFetcherFactoryImpl.class);

  private final Pipeline pipeline;

  @Autowired
  public DataFetcherFactoryImpl(Pipeline pipeline) {
    this.pipeline = pipeline;
  }

  @Override
  public <S> DataFetcher<GetResult<S>> getGetDataFetcher(final Class<S> stateClass) {

    LOGGER.info("Creating get DataFetcher for stateClass: {}", stateClass);
    return new GetDataFetcherImpl<S>(pipeline, stateClass);
  }

  @Override
  public <S, A> DataFetcher<SetResult<S>> getSetDataFetcher(
      final Class<S> stateClass, final Class<A> actionClass) {

    LOGGER.info(
        "Creating set DataFetcher for stateClass: {}, actionClass: {}", stateClass, actionClass);
    return new SetDataFetcherImpl<S, A>(pipeline, stateClass, actionClass);
  }
}
