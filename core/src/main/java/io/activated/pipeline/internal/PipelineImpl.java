package io.activated.pipeline.internal;

import io.activated.pipeline.*;
import io.activated.pipeline.key.Key;
import io.activated.pipeline.repository.StateRepository;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import org.reactivestreams.Publisher;

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
  public <S> GetResult<S> get(Class<S> stateType) {

    var result = new GetResult<S>();
    result.setState(stateAccess.get(stateType));
    return result;
  }

  @Override
  public <S, A> Publisher<SetResult<S>> set(Class<S> stateType, A action) {

    Class<A> actionType = (Class<A>) action.getClass();

    var state = stateAccess.get(stateType);
    var stateName = stateType.getCanonicalName();

    var reducer = registry.getReducer(ReducerKey.create(stateType, actionType));
    var keyStrategy = registry.getKeyStrategy(stateType);

    var key = keyStrategy.get();

    var before = snapshotter.snapshot(state);
    var actionSnapshot = snapshotter.snapshot(action);

    return Flowable.fromPublisher(reducer.reduce(state, action)).doOnEach(n -> {
      if (n.isOnNext()) {

        var s = n.getValue();

        storeAndDiff(actionType, state, stateName, key, before, actionSnapshot, s);
      }
    }).onErrorReturn(e -> {

      if (isClearState(e) || isClearAllStates(e)) {
        if (isClearState(e)) {
          stateRepository.clear(key.getValue(), stateName);
          changeLogger.change(
                  key, stateName, actionType.getCanonicalName(), actionSnapshot, Diff.CLEAR);
        } else if (isClearAllStates(e)) {
          for (var _stateType : registry.getStateTypes()) {
            var _stateName = _stateType.getCanonicalName();
            stateRepository.clear(key.getValue(), _stateName);
            changeLogger.change(
                    key, _stateName, actionType.getCanonicalName(), actionSnapshot, Diff.CLEAR);
          }
        }

        if (isIgnore(e)) {
          return stateAccess.zero(stateType);
        } else {
          throw new PipelineException(e);
        }
      }

      if (isIgnore(e)) {

        S s = ((Ignore)e).returnInstead();

        storeAndDiff(actionType, state, stateName, key, before, actionSnapshot, s);

        return s;
      } else {
        throw new PipelineException(e);
      }

    }).map(r -> {

      var result = new SetResult<S>();
      result.setState(r);
      return result;

    });

  }

  private <S, A> void storeAndDiff(Class<A> actionType, S state, String stateName, Key key, Snapshot before, Snapshot actionSnapshot, S s) {

    stateRepository.set(key.getValue(), stateName, s);

    var after = snapshotter.snapshot(state);
    var diff = after.diff(before);

    changeLogger.change(key, stateName, actionType.getCanonicalName(), actionSnapshot, diff);
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
