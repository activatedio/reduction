package io.activated.pipeline.fixtures;

import io.activated.pipeline.BlockingReducer;
import io.activated.pipeline.Reducer;

public class DummyReducer implements BlockingReducer<DummyState, DummyAction> {

  @Override
  public void blockingReduce(DummyState state, DummyAction action) {}
}
