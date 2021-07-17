package io.activated.pipeline.fixtures;

import io.activated.pipeline.InitialState;
import io.activated.pipeline.repository.Dummy;

public class DummyInitialState implements InitialState<Dummy> {
  @Override
  public Dummy initial() {
    return null;
  }
}
