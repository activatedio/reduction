package io.activated.pipeline.fixtures;

import io.activated.pipeline.InitialState;
import io.activated.pipeline.repository.Dummy;
import org.reactivestreams.Publisher;

public class DummyInitialState implements InitialState<Dummy> {

  @Override
  public Publisher<Dummy> initial() {
    return null;
  }

  @Override
  public Dummy zero() {
    return null;
  }
}
