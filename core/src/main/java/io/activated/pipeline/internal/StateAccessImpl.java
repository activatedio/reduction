package io.activated.pipeline.internal;

import io.activated.pipeline.StateAccess;
import io.activated.pipeline.repository.StateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateAccessImpl implements StateAccess {

  private static final Logger logger = LoggerFactory.getLogger(StateAccessImpl.class);

  private final Registry registry;
  private final StateRepository stateRepository;
  private final Snapshotter snapshotter;
  private final ChangeLogger changeLogger;

  public StateAccessImpl(
      Registry registry,
      StateRepository stateRepository,
      Snapshotter snapshotter,
      ChangeLogger changeLogger) {
    this.registry = registry;
    this.stateRepository = stateRepository;
    this.snapshotter = snapshotter;
    this.changeLogger = changeLogger;
  }

  @Override
  public <S> S get(Class<S> stateType) {

    var stateName = stateType.getCanonicalName();
    var keyStrategy = registry.getKeyStrategy(stateType);

    var key = keyStrategy.get();

    var keyExists = stateRepository.exists(key.getValue(), stateName);

    if (key.getMoveFrom() != null) {
      var moveFromExists = stateRepository.exists(key.getMoveFrom(), stateName);

      if (moveFromExists && !keyExists) {
        logger.info("Upgrading state from [{}] to [{}]", key.getMoveFrom(), key.getValue());
        stateRepository.moveKey(key.getMoveFrom(), key.getValue(), stateName);
        changeLogger.moveKey(key);
        keyExists = true;
      } else if (moveFromExists) {
        logger.warn(
            "State exists at both previous key [{}] and current key [{}]. Previous keyed state hidden by new keyed state.",
            key.getMoveFrom(),
            key.getValue());
      }
    }

    if (keyExists) {
      return stateRepository.get(key.getValue(), stateName, stateType);
    } else {
      var state = initial(stateType);
      stateRepository.set(key.getValue(), stateName, state);
      changeLogger.initial(key, stateName, snapshotter.snapshot(state));
      return state;
    }
  }

  @Override
  public <S> S zero(Class<S> stateType) {
    var initial = registry.getInitial(InitialStateKey.create(stateType));
    return initial.zero();
  }

  private <S> S initial(Class<S> stateType) {

    var initial = registry.getInitial(InitialStateKey.create(stateType));
    return initial.initial();
  }
}
