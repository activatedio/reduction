package io.activated.pipeline.micronaut.fixtures;

import io.activated.pipeline.Context;
import io.activated.pipeline.InitialState;
import io.activated.pipeline.annotations.Initial;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Initial
@Singleton
public class DummyInitialState implements InitialState<DummyState> {
  @Override
  public Mono<DummyState> initial(Context context) {
    return Mono.empty();
  }

  @Override
  public DummyState zero() {
    return null;
  }
}
