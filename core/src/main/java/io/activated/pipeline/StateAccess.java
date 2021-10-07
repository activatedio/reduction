package io.activated.pipeline;

import org.reactivestreams.Publisher;

public interface StateAccess {
  /**
   * Gets object, initializing if not present
   *
   * @param <S> type of state
   * @param stateType
   * @return state of object
   */
  <S> Publisher<S> get(Class<S> stateType);

  /**
   * Gets the zero representation of the object
   *
   * @param stateType
   * @param <S> type of state
   * @return zero state of object
   */
  <S> S zero(Class<S> stateType);
}
