package io.activated.pipeline;

/**
 * Protects access to the state.
 *
 * @param <S> type of state
 */
public interface StateGuard<S> {

  /** If access is disallowed for any state it will throw an exception */
  void guardGlobal();

  /**
   * If access is disallowed for a specific state it will throw an exception
   *
   * @param state
   */
  void guard(S state);
}
