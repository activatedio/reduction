package io.activated.pipeline.micronaut;

import graphql.schema.DataFetcher;
import io.activated.pipeline.Exportable;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.SetResult;
import java.util.concurrent.CompletableFuture;

public interface DataFetcherFactory {

  <S> DataFetcher<CompletableFuture<GetResult<S>>> getGetDataFetcher(Class<S> stateClass);

  <I extends Exportable<E>, E>
      DataFetcher<CompletableFuture<GetResult<E>>> getExportableGetDataFetcher(Class<?> stateClass);

  <S, A> DataFetcher<CompletableFuture<SetResult<S>>> getSetDataFetcher(
      Class<S> stateClass, Class<A> actionClass);

  <I extends Exportable<E>, E, A>
      DataFetcher<CompletableFuture<SetResult<E>>> getExportableSetDataFetcher(
          Class<?> stateClass, Class<A> actionClass);
}
