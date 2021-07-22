package io.activated.pipeline.micronaut.fixtures;

import io.activated.pipeline.Reducer;
import io.activated.pipeline.annotations.Operation;
import javax.inject.Singleton;

@Operation
@Singleton
public class DummyReducer implements Reducer<DummyState, DummyAction> {

  @Override
  public void reduce(final DummyState state, final DummyAction action) {}
}
