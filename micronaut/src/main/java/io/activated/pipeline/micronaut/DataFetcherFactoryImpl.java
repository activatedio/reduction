package io.activated.pipeline.micronaut;

import graphql.schema.DataFetcher;
import io.activated.pipeline.Exportable;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import io.activated.pipeline.micronaut.internal.Exporter;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;
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
    return new GetDataFetcherImpl(contextFactory, pipeline, stateClass, s -> s);
  }

  @Override
  public <S extends Exportable<E>, E>
      DataFetcher<CompletableFuture<GetResult<E>>> getExportableGetDataFetcher(
          Class<?> stateClass) {

    LOGGER.debug("Creating exportable get DataFetcher for stateClass: {}", stateClass);
    return new GetDataFetcherImpl<>(
        contextFactory, pipeline, (Class<S>) stateClass, new Exporter<>());
  }

  @Override
  public <S, A> DataFetcher<CompletableFuture<SetResult<S>>> getSetDataFetcher(
      final Class<S> stateClass, final Class<A> actionClass) {

    LOGGER.debug(
        "Creating set DataFetcher for stateClass: {}, actionClass: {}", stateClass, actionClass);
    return new SetDataFetcherImpl<>(contextFactory, pipeline, stateClass, actionClass, s -> s);
  }

  @Override
  public <S extends Exportable<E>, E, A>
      DataFetcher<CompletableFuture<SetResult<E>>> getExportableSetDataFetcher(
          final Class<?> stateClass, final Class<A> actionClass) {

    LOGGER.debug(
        "Creating set DataFetcher for stateClass: {}, actionClass: {}", stateClass, actionClass);
    return new SetDataFetcherImpl<>(
        contextFactory, pipeline, (Class<S>) stateClass, actionClass, new Exporter<>());
  }
}
