package io.activated.pipeline.server.fixtures;

import io.activated.pipeline.Reducer;

public class DummyReducer implements Reducer<DummyState, DummyAction> {

  @Override
  public void reduce(final DummyState state, final DummyAction action) {}
}
