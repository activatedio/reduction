package io.activated.pipeline.internal;

import io.activated.objectdiff.Snapshotter;
import io.activated.pipeline.Context;
import io.activated.pipeline.StateAccess;
import io.activated.pipeline.key.Key;
import io.activated.pipeline.repository.StateRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class StateAccessImpl implements StateAccess {

  private static class KeyExists {

    private final Key key;
    private final boolean exists;

    private KeyExists(Key key, boolean exists) {
      this.key = key;
      this.exists = exists;
    }
  }

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
  public <S> Mono<S> get(Context context, Class<S> stateType) {

    var stateName = stateType.getCanonicalName();

    return Mono.fromCallable(() -> registry.getKeyStrategy(stateType))
        .flatMap(ks -> ks.apply(context))
        .flatMap(
            key ->
                stateRepository
                    .exists(key.getValue(), stateName)
                    .map(
                        exists -> {
                          return new KeyExists(key, exists);
                        }))
        .flatMap(
            keyExists -> {
              var key = keyExists.key;
              if (key.getMoveFrom() == null) {
                return Mono.just(keyExists);
              } else {
                return stateRepository
                    .exists(key.getMoveFrom(), stateName)
                    .flatMap(
                        moveFromExists -> {
                          if (moveFromExists && !keyExists.exists) {
                            return stateRepository
                                .moveKey(key.getMoveFrom(), key.getValue(), stateName)
                                .doOnSuccess(
                                    v -> {
                                      changeLogger.moveKey(key);
                                    })
                                .map(v -> keyExists)
                                .switchIfEmpty(
                                    Mono.just(true).map(b -> new KeyExists(keyExists.key, b)));
                          } else if (moveFromExists) {
                            logger.warn(
                                "State exists at both previous key [{}] and current key [{}]. Previous keyed state hidden by new keyed state.",
                                key.getMoveFrom(),
                                key.getValue());
                            return Mono.just(keyExists);
                          } else {
                            return Mono.just(keyExists);
                          }
                        });
              }
            })
        .flatMap(
            keyExists -> {
              if (keyExists.exists) {
                return stateRepository
                    .get(keyExists.key.getValue(), stateName, stateType)
                    .map(Optional::get);
              } else {
                var state = initial(stateType);
                return stateRepository
                    .set(keyExists.key.getValue(), stateName, state)
                    .doOnSuccess(
                        v -> {
                          changeLogger.initial(
                              keyExists.key, stateName, snapshotter.snapshot(state));
                        })
                    .map(v -> state)
                    .defaultIfEmpty(state);
              }
            });
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
