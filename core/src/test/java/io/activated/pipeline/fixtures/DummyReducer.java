package io.activated.pipeline.fixtures;

import io.activated.pipeline.BlockingReducer;
import io.activated.pipeline.Context;

public class DummyReducer implements BlockingReducer<DummyState, DummyAction> {

  @Override
  public void blockingReduce(Context context, DummyState state, DummyAction action) {}
}
