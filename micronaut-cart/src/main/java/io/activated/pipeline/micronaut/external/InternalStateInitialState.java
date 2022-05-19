package io.activated.pipeline.micronaut.external;

import io.activated.pipeline.InitialState;
import io.activated.pipeline.annotations.Initial;
import jakarta.inject.Singleton;

@Initial
@Singleton
public class InternalStateInitialState implements InitialState<InternalState> {

  @Override
  public InternalState initial() {
    return new InternalState();
  }

  @Override
  public InternalState zero() {
    return new InternalState();
  }
}
