package io.activated.pipeline.micronaut;

import graphql.schema.DataFetcher;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.activated.pipeline.env.SessionIdSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@Singleton
public class DataFetcherFactoryImpl implements DataFetcherFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataFetcherFactoryImpl.class);

  private final Pipeline pipeline;
  private final SessionIdSupplier sessionIdSupplier;

  @Inject
  public DataFetcherFactoryImpl(Pipeline pipeline, @Named("request") SessionIdSupplier sessionIdSupplier) {
    this.pipeline = pipeline;
    this.sessionIdSupplier = sessionIdSupplier;
  }

  @Override
  public <S> DataFetcher<CompletableFuture<GetResult<S>>> getGetDataFetcher(final Class<S> stateClass) {

    LOGGER.debug("Creating get DataFetcher for stateClass: {}", stateClass);
    return new GetDataFetcherImpl<S>(pipeline, sessionIdSupplier, stateClass);
  }

  @Override
  public <S, A> DataFetcher<CompletableFuture<SetResult<S>>> getSetDataFetcher(
      final Class<S> stateClass, final Class<A> actionClass) {

    LOGGER.debug(
        "Creating set DataFetcher for stateClass: {}, actionClass: {}", stateClass, actionClass);
    return new SetDataFetcherImpl<S, A>(pipeline, sessionIdSupplier, stateClass, actionClass);
  }
}
