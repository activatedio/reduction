package io.activated.pipeline.internal;

import io.activated.pipeline.Context;
import io.activated.pipeline.StateAccess;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class GuardedStateAccess implements StateAccess {

  private final Registry registry;
  private final StateAccess delegate;

  public GuardedStateAccess(Registry registry, StateAccess delegate) {
    this.registry = registry;
    this.delegate = delegate;
  }

  @Override
  public <S> Publisher<S> get(Context context, Class<S> stateType) {

    var stateGuards = registry.getStateGuards(stateType);

    for (var stateGuard : stateGuards) {
      stateGuard.guardGlobal();
    }

    return Mono.from(delegate.get(context, stateType))
        .publishOn(Schedulers.parallel())
        .doOnNext(
            state -> {
              for (var stateGuard : stateGuards) {
                stateGuard.guard(context, state);
              }
            });
  }

  @Override
  // TODO - Do we need go guard the zero state?
  public <S> S zero(Class<S> stateType) {
    return delegate.zero(stateType);
  }
}
