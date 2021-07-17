package io.activated.pipeline.internal;

import io.activated.pipeline.*;
import io.activated.pipeline.repository.StateRepository;

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
  public <S, A> SetResult<S> set(Class<S> stateType, A action) {

    Class<A> actionType = (Class<A>) action.getClass();

    var state = stateAccess.get(stateType);
    var stateName = stateType.getCanonicalName();

    var reducer = registry.getReducer(ReducerKey.create(stateType, actionType));
    var keyStrategy = registry.getKeyStrategy(stateType);

    var key = keyStrategy.get();

    var before = snapshotter.snapshot(state);
    var actionSnapshot = snapshotter.snapshot(action);

    try {

      reducer.reduce(state, action);

    } catch (RuntimeException e) {

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
          return new SetResult<S>();
        } else {
          throw e;
        }
      }

      if (!(isIgnore(e))) {
        throw e;
      }
    }

    stateRepository.set(key.getValue(), stateName, state);

    var after = snapshotter.snapshot(state);
    var diff = after.diff(before);

    changeLogger.change(key, stateName, actionType.getCanonicalName(), actionSnapshot, diff);

    var result = new SetResult<S>();
    result.setState(state);
    return result;
  }

  private static boolean isClearState(RuntimeException e) {
    return ClearState.class.isAssignableFrom(e.getClass());
  }

  private static boolean isClearAllStates(RuntimeException e) {
    return ClearAllStates.class.isAssignableFrom(e.getClass());
  }

  private static boolean isIgnore(RuntimeException e) {
    return Ignore.class.isAssignableFrom(e.getClass());
  }
}
