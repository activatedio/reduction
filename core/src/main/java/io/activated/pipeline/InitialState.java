package io.activated.pipeline;

import reactor.core.publisher.Mono;

public interface InitialState<S> {
  /**
   * The initialized state
   *
   * @return
   */
  Mono<S> initial(Context context);

  /**
   * Representation of the zero value of the state. Usually a new instance of the object
   *
   * @return
   */
  S zero();
}
