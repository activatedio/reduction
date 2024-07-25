package io.activated.pipeline;

import org.reactivestreams.Publisher;

public interface InitialState<S> {
  /**
   * The initialized state
   *
   * @return
   */
  Publisher<S> initial();

  /**
   * Representation of the zero value of the state. Usually a new instance of the object
   *
   * @return
   */
  S zero();
}
