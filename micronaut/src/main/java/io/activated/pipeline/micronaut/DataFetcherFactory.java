package io.activated.pipeline.micronaut;

import graphql.schema.DataFetcher;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.SetResult;

public interface DataFetcherFactory {

  <S> DataFetcher<GetResult<S>> getGetDataFetcher(Class<S> stateClass);

  <S, A> DataFetcher<SetResult<S>> getSetDataFetcher(Class<S> stateClass, Class<A> actionClass);
}