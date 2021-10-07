package io.activated.pipeline.micronaut.fixtures;

import io.activated.pipeline.BlockingReducer;
import io.activated.pipeline.annotations.Operation;
import javax.inject.Singleton;

@Operation
@Singleton
public class DummyReducer implements BlockingReducer<DummyState, DummyAction> {

  @Override
  public void blockingReduce(DummyState state, DummyAction action) {}
}
