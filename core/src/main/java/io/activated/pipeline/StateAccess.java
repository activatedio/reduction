package io.activated.pipeline;

import reactor.core.publisher.Mono;

public interface StateAccess {
  /**
   * Gets object, initializing if not present
   *
   * @param <S> type of state
   * @param stateType
   * @return state of object
   */
  <S> Mono<S> get(Context context, Class<S> stateType);

  /**
   * Gets the zero representation of the object
   *
   * @param stateType
   * @param <S> type of state
   * @return zero state of object
   */
  <S> S zero(Class<S> stateType);
}
