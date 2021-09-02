package io.activated.pipeline.micronaut.fixtures;

import io.activated.pipeline.InitialState;
import io.activated.pipeline.annotations.Initial;
import javax.inject.Singleton;

@Initial
@Singleton
public class DummyInitialState implements InitialState<DummyState> {
  @Override
  public DummyState initial() {
    return null;
  }

  @Override
  public DummyState zero() {
    return null;
  }
}
