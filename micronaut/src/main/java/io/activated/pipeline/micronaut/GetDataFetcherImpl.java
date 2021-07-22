package io.activated.pipeline.micronaut;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;

public class GetDataFetcherImpl<S> implements DataFetcher<GetResult<S>> {

  private final Pipeline pipeline;
  private final Class<S> stateClass;

  public GetDataFetcherImpl(Pipeline pipeline, Class<S> stateClass) {
    this.pipeline = pipeline;
    this.stateClass = stateClass;
  }

  @Override
  public GetResult<S> get(final DataFetchingEnvironment environment) throws Exception {
    return pipeline.get(stateClass);
  }
}
