package io.activated.pipeline.micronaut.external;

import io.activated.pipeline.Context;
import io.activated.pipeline.InitialState;
import io.activated.pipeline.annotations.Initial;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Initial
@Singleton
public class InternalStateInitialState implements InitialState<InternalState> {

  @Override
  public Mono<InternalState> initial(Context context) {
    return Mono.just(new InternalState());
  }

  @Override
  public InternalState zero() {
    return new InternalState();
  }
}
