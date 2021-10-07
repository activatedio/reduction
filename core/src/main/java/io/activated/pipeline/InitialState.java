package io.activated.pipeline;

public interface InitialState<S> {
  /**
   * The initialized state
   *
   * @return
   */
  S initial();

  /**
   * Representation of the zero value of the state. Usually a new instance of the object
   *
   * @return
   */
  S zero();
}
