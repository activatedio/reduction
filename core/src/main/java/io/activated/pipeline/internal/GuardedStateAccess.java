package io.activated.pipeline.internal;

import io.activated.pipeline.StateAccess;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public class GuardedStateAccess implements StateAccess {

  private final Registry registry;
  private final StateAccess delegate;

  public GuardedStateAccess(Registry registry, StateAccess delegate) {
    this.registry = registry;
    this.delegate = delegate;
  }

  @Override
  public <S> Publisher<S> get(Class<S> stateType) {

    var stateGuards = registry.getStateGuards(stateType);

    for (var stateGuard : stateGuards) {
      stateGuard.guardGlobal();
    }

    return Mono.from(delegate.get(stateType)).doOnNext(state -> {
      for (var stateGuard : stateGuards) {
        stateGuard.guard(state);
      }
    });
  }

  @Override
  // TODO - Do we need go guard the zero state?
  public <S> S zero(Class<S> stateType) {
    return delegate.zero(stateType);
  }
}
