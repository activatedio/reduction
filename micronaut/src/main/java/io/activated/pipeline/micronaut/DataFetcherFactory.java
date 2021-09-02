package io.activated.pipeline.micronaut;

import graphql.schema.DataFetcher;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.SetResult;
import org.reactivestreams.Publisher;

import java.util.concurrent.CompletableFuture;

public interface DataFetcherFactory {

  <S> DataFetcher<GetResult<S>> getGetDataFetcher(Class<S> stateClass);

  <S, A> DataFetcher<CompletableFuture<SetResult<S>>> getSetDataFetcher(Class<S> stateClass, Class<A> actionClass);
}
