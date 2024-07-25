package io.activated.pipeline.micronaut.fixtures;

import io.activated.pipeline.InitialState;
import io.activated.pipeline.annotations.Initial;
import javax.inject.Singleton;
import org.reactivestreams.Publisher;

@Initial
@Singleton
public class DummyInitialState implements InitialState<DummyState> {
  @Override
  public Publisher<DummyState> initial() {
    return null;
  }

  @Override
  public DummyState zero() {
    return null;
  }
}
