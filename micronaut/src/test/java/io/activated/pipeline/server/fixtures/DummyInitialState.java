package io.activated.pipeline.server.fixtures;

import io.activated.pipeline.InitialState;

public class DummyInitialState implements InitialState<Dummy1> {
  @Override
  public Dummy1 initial() {
    return null;
  }
}
