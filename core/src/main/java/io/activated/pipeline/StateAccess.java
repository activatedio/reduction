package io.activated.pipeline;

public interface StateAccess {
  /**
   * Gets object, initializing if not present
   * @param stateType
   * @param <S> type of state
   * @return state of object
   */
  <S> S get(Class<S> stateType);

  /**
   * Gets the zero representation of the object
   * @param stateType
   * @param <S> type of state
   * @return zero state of object
   */
  <S> S zero(Class<S> stateType);
}
