package io.activated.pipeline.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import io.activated.objectdiff.Snapshot;
import io.activated.objectdiff.Snapshotter;
import io.activated.pipeline.InitialState;
import io.activated.pipeline.fixtures.DummyAction;
import io.activated.pipeline.fixtures.DummyState;
import io.activated.pipeline.key.Key;
import io.activated.pipeline.key.KeyStrategy;
import io.activated.pipeline.repository.StateRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;

@ExtendWith({MockitoExtension.class})
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class StateAccessImplTest {

  private StateAccessImpl unit;

  private final String keyValue = "test-key-value";
  private final String keyMoveFrom = "test-key-move-from";
  private final Key key;

  {
    key = new Key();
    key.setValue(keyValue);
  }

  private final DummyState state;

  {
    state = new DummyState();
    state.setValue("test-value");
  }

  private final DummyAction action = new DummyAction();
  private final Class<DummyState> stateType = DummyState.class;
  private final String stateName = stateType.getCanonicalName();

  @Mock private StateRepository stateRepository;
  @Mock private Registry registry;
  @Mock private InitialState<DummyState> initialState;
  @Mock private KeyStrategy keyStrategy;
  @Mock private Snapshotter snapshotter;
  @Mock private ChangeLogger changeLogger;
  @Mock private Snapshot snapshot;

  @BeforeEach
  public void setUp() {
    unit = new StateAccessImpl(registry, stateRepository, snapshotter, changeLogger);
  }

  private void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
        stateRepository, registry, initialState, keyStrategy, snapshotter, changeLogger);
  }

  @Test
  public void get_noMoveFrom_currentNotExisting() {

    when(registry.getKeyStrategy(stateType)).thenReturn(keyStrategy);
    when(keyStrategy.get()).thenReturn(Mono.just(key));
    when(stateRepository.exists(key.getValue(), stateName)).thenReturn(Mono.just(false));
    when(registry.getInitial(InitialStateKey.create(stateType))).thenReturn(initialState);
    when(initialState.initial()).thenReturn(state);
    when(stateRepository.set(key.getValue(), stateName, state)).thenReturn(Mono.empty());
    when(snapshotter.snapshot(state)).thenReturn(snapshot);

    var got = Mono.from(unit.get(stateType)).block();

    assertThat(got).isSameAs(state);

    verify(registry).getKeyStrategy(stateType);
    verify(keyStrategy).get();
    verify(stateRepository).exists(key.getValue(), stateName);
    verify(registry).getInitial(InitialStateKey.create(stateType));
    verify(initialState).initial();
    verify(stateRepository).set(key.getValue(), stateName, state);
    verify(changeLogger).initial(key, stateName, snapshot);

    verifyNoMoreInteractions();
  }

  @Test
  public void get_noMoveFrom_currentExisting() {
    when(registry.getKeyStrategy(stateType)).thenReturn(keyStrategy);
    when(keyStrategy.get()).thenReturn(Mono.just(key));
    when(stateRepository.exists(key.getValue(), stateName)).thenReturn(Mono.just(true));
    when(stateRepository.get(key.getValue(), stateName, stateType))
        .thenReturn(Mono.just(Optional.of(state)));

    var got = Mono.from(unit.get(stateType)).block();

    assertThat(got).isEqualTo(state);

    verify(registry).getKeyStrategy(stateType);
    verify(keyStrategy).get();
    verify(stateRepository).exists(key.getValue(), stateName);
    verify(stateRepository).get(key.getValue(), stateName, stateType);

    verifyNoMoreInteractions();
  }

  @Test
  public void get_moveFrom_neitherExisting() {
    key.setMoveFrom(keyMoveFrom);

    when(registry.getKeyStrategy(stateType)).thenReturn(keyStrategy);
    when(keyStrategy.get()).thenReturn(Mono.just(key));
    when(stateRepository.exists(key.getMoveFrom(), stateName)).thenReturn(Mono.just(false));
    when(stateRepository.exists(key.getValue(), stateName)).thenReturn(Mono.just(false));
    when(registry.getInitial(InitialStateKey.create(stateType))).thenReturn(initialState);
    when(initialState.initial()).thenReturn(state);
    when(stateRepository.set(key.getValue(), stateName, state)).thenReturn(Mono.empty());
    when(snapshotter.snapshot(state)).thenReturn(snapshot);

    var got = Mono.from(unit.get(stateType)).block();
    assertThat(got).isSameAs(state);

    verify(registry).getKeyStrategy(stateType);
    verify(keyStrategy).get();
    verify(stateRepository).exists(key.getMoveFrom(), stateName);
    verify(stateRepository).exists(key.getValue(), stateName);
    verify(registry).getInitial(InitialStateKey.create(stateType));
    verify(initialState).initial();
    verify(stateRepository).set(key.getValue(), stateName, state);
    verify(changeLogger).initial(key, stateName, snapshot);

    verifyNoMoreInteractions();
  }

  @Test
  public void get_moveFrom_moveFromNotExisting_currentExisting() {

    key.setMoveFrom(keyMoveFrom);

    when(registry.getKeyStrategy(stateType)).thenReturn(keyStrategy);
    when(keyStrategy.get()).thenReturn(Mono.just(key));
    when(stateRepository.exists(key.getMoveFrom(), stateName)).thenReturn(Mono.just(false));
    when(stateRepository.exists(key.getValue(), stateName)).thenReturn(Mono.just(true));
    when(stateRepository.get(key.getValue(), stateName, stateType))
        .thenReturn(Mono.just(Optional.of(state)));

    var got = Mono.from(unit.get(stateType)).block();
    assertThat(got).isSameAs(state);

    verify(registry).getKeyStrategy(stateType);
    verify(keyStrategy).get();
    verify(stateRepository).exists(key.getMoveFrom(), stateName);
    verify(stateRepository).exists(key.getValue(), stateName);
    verify(stateRepository).get(key.getValue(), stateName, stateType);

    verifyNoMoreInteractions();
  }

  @Test
  public void get_moveFrom_moveFromExisting_currentNotExisting() {

    key.setMoveFrom(keyMoveFrom);

    when(registry.getKeyStrategy(stateType)).thenReturn(keyStrategy);
    when(keyStrategy.get()).thenReturn(Mono.just(key));
    when(stateRepository.exists(key.getMoveFrom(), stateName)).thenReturn(Mono.just(true));
    when(stateRepository.exists(key.getValue(), stateName)).thenReturn(Mono.just(false));
    when(stateRepository.moveKey(key.getMoveFrom(), key.getValue(), stateName))
        .thenReturn(Mono.just(true).then());
    when(stateRepository.get(key.getValue(), stateName, stateType))
        .thenReturn(Mono.just(Optional.of(state)));

    var got = Mono.from(unit.get(stateType)).block();
    assertThat(got).isSameAs(state);

    verify(registry).getKeyStrategy(stateType);
    verify(keyStrategy).get();
    verify(stateRepository).exists(key.getMoveFrom(), stateName);
    verify(stateRepository).exists(key.getValue(), stateName);
    verify(stateRepository).moveKey(key.getMoveFrom(), key.getValue(), stateName);
    verify(changeLogger).moveKey(key);
    verify(stateRepository).get(key.getValue(), stateName, stateType);

    verifyNoMoreInteractions();
  }

  @Test
  public void zero() {

    when(registry.getInitial(InitialStateKey.create(stateType))).thenReturn(initialState);
    when(initialState.zero()).thenReturn(state);

    var got = unit.zero(stateType);

    assertThat(got).isSameAs(state);

    verify(registry).getInitial(InitialStateKey.create(stateType));
    verify(initialState).zero();

    verifyNoMoreInteractions();
  }
}
