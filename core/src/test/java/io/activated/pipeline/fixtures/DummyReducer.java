package io.activated.pipeline.fixtures;

import io.activated.pipeline.BlockingReducer;

public class DummyReducer implements BlockingReducer<DummyState, DummyAction> {

  @Override
  public void blockingReduce(DummyState state, DummyAction action) {}
}
