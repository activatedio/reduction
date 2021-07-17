package io.activated.pipeline.fixtures;

import io.activated.pipeline.Reducer;

public class DummyReducer implements Reducer<DummyState, DummyAction> {

  @Override
  public void reduce(DummyState state, DummyAction action) {}
}
