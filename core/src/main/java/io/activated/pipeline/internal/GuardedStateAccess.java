package io.activated.pipeline.internal;

import io.activated.pipeline.StateAccess;

public class GuardedStateAccess implements StateAccess {

  private final Registry registry;
  private final StateAccess delegate;

  public GuardedStateAccess(Registry registry, StateAccess delegate) {
    this.registry = registry;
    this.delegate = delegate;
  }

  @Override
  public <S> S get(Class<S> stateType) {

    var stateGuards = registry.getStateGuards(stateType);

    for (var stateGuard : stateGuards) {
      stateGuard.guardGlobal();
    }

    var state = delegate.get(stateType);

    for (var stateGuard : stateGuards) {
      stateGuard.guard(state);
    }

    return state;
  }
}
