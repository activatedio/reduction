package io.activated.pipeline.internal;

import io.activated.objectdiff.Diff;
import io.activated.objectdiff.Snapshot;
import io.activated.objectdiff.Snapshotter;
import io.activated.pipeline.*;
import io.activated.pipeline.key.Key;
import io.activated.pipeline.repository.StateRepository;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.sql.Ref;

public class PipelineImpl implements Pipeline {

  private final Registry registry;
  private final StateAccess stateAccess;
  private final StateRepository stateRepository;
  private final Snapshotter snapshotter;
  private final ChangeLogger changeLogger;

  public PipelineImpl(
      Registry registry,
      StateAccess stateAccess,
      StateRepository stateRepository,
      Snapshotter snapshotter,
      ChangeLogger changeLogger) {
    this.registry = registry;
    this.stateAccess = stateAccess;
    this.stateRepository = stateRepository;
    this.snapshotter = snapshotter;
    this.changeLogger = changeLogger;
  }

  @Override
  public <S> Publisher<GetResult<S>> get(Class<S> stateType) {


    // TODO - this is an untested hack. Please fix
    try {
      var reducer = registry.getReducer(ReducerKey.create(stateType, RefreshAction.class));
      return Mono.from(set(stateType, new RefreshAction())).map(r -> {
        var result = new GetResult<S>();
        result.setState(r.getState());
        return result;
      });
    } catch (RuntimeException e) {
      return Mono.from(stateAccess.get(stateType)).map(s -> {
        var result = new GetResult<S>();
        result.setState(s);
        return result;
      });
    }
  }

  @Override
  public <S, A> Publisher<SetResult<S>> set(Class<S> stateType, A action) {

    Class<A> actionType = (Class<A>) action.getClass();

    return Flowable.fromPublisher(stateAccess.get(stateType)).flatMap(state -> {
      var stateName = stateType.getCanonicalName();

      var reducer = registry.getReducer(ReducerKey.create(stateType, actionType));
      var keyStrategy = registry.getKeyStrategy(stateType);

      var key = keyStrategy.get();

      var before = snapshotter.snapshot(state);
      var actionSnapshot = snapshotter.snapshot(action);

      return Flowable.fromPublisher(reducer.reduce(state, action))
              .flatMap(v ->
                     Mono.from(storeAndDiff(actionType, state, stateName, key, before, actionSnapshot, v))
                             .map(_v -> v)
                             .defaultIfEmpty(v))
      .onErrorResumeNext(e -> {
        if (isClearState(e) || isClearAllStates(e)) {
          if (isClearState(e)) {
            // TODO - The clear actually isn't working here
            // TODO - Change this in the future to not block
            // The map to state is never called since it is empty - just used to signal
            return Mono.from(stateRepository.clear(key.getValue(), stateName)).map(v -> state)
                    .doOnSuccess(s -> {
                      changeLogger.change(
                              key, stateName, actionType.getCanonicalName(), actionSnapshot, Diff.CLEAR);
                    })
                    .switchIfEmpty(Mono.fromCallable(() -> {
                      if (isIgnore(e)) {
                        return stateAccess.zero(stateType);
                      } else {
                        throw new PipelineException(e);
                      }
                    }));
          } else if (isClearAllStates(e)) {

            throw new UnsupportedOperationException("clear all states not yet supported");
            /*
            for (var _stateType : registry.getStateTypes()) {
              var _stateName = _stateType.getCanonicalName();
              // TODO - The clear actually isn't working here
              Mono.from(stateRepository.clear(key.getValue(), _stateName))
                      .publishOn(Schedulers.boundedElastic()).log().block();
              changeLogger.change(
                      key, _stateName, actionType.getCanonicalName(), actionSnapshot, Diff.CLEAR);
            }
             */
          }
        }

        if (isIgnore(e)) {

          S s = ((Ignore)e).returnInstead();

          return Mono.from(storeAndDiff(actionType, state, stateName, key, before, actionSnapshot, s))
                  .map(_v -> s).defaultIfEmpty(s);

        } else {
          throw new PipelineException(e);
        }

      }).map(r -> {

        var result = new SetResult<S>();
        result.setState(r);
        return result;

      });
    });
  }

  private <S, A> Publisher<Void> storeAndDiff(Class<A> actionType, S state, String stateName, Key key, Snapshot before, Snapshot actionSnapshot, S s) {

    return Mono.from(stateRepository.set(key.getValue(), stateName, s)).doOnSuccess(v -> {
      var after = snapshotter.snapshot(state);
      var diff = after.diff(before);

      changeLogger.change(key, stateName, actionType.getCanonicalName(), actionSnapshot, diff);
    });

  }

  private static boolean isClearState(Throwable t) {
    return ClearState.class.isAssignableFrom(t.getClass());
  }

  private static boolean isClearAllStates(Throwable t) {
    return ClearAllStates.class.isAssignableFrom(t.getClass());
  }

  private static boolean isIgnore(Throwable t) {
    return Ignore.class.isAssignableFrom(t.getClass());
  }
}
