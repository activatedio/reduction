package io.activated.pipeline.fixtures;

import io.activated.pipeline.Context;
import io.activated.pipeline.InitialState;
import io.activated.pipeline.repository.Dummy;
import reactor.core.publisher.Mono;

public class DummyInitialState implements InitialState<Dummy> {

  @Override
  public Mono<Dummy> initial(Context context) {
    return Mono.empty();
  }

  @Override
  public Dummy zero() {
    return null;
  }
}
