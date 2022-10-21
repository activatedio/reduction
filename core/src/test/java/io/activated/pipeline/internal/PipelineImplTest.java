package io.activated.pipeline.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

import io.activated.objectdiff.Diff;
import io.activated.objectdiff.Snapshot;
import io.activated.objectdiff.Snapshotter;
import io.activated.pipeline.*;
import io.activated.pipeline.fixtures.DummyAction;
import io.activated.pipeline.fixtures.DummyState;
import io.activated.pipeline.fixtures.DummyState2;
import io.activated.pipeline.key.Key;
import io.activated.pipeline.key.KeyStrategy;
import io.activated.pipeline.repository.StateRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
public class PipelineImplTest {

  public static class ClearStateException extends RuntimeException implements ClearState {}

  public static class ClearAllStatesException extends RuntimeException implements ClearAllStates {}

  @Mock private Registry registry;
  @Mock private StateAccess stateAccess;
  @Mock private StateRepository stateRepository;
  @Mock private Reducer<DummyState, DummyAction> reducer;
  @Mock private KeyStrategy keyStrategy;
  @Mock private Snapshotter snapshotter;
  @Mock private ChangeLogger changeLogger;

  @Mock private Snapshot beforeSnapshot;
  @Mock private Snapshot actionSnapshot;
  @Mock private Snapshot afterSnapshot;
  @Mock private Diff diff;

  private PipelineImpl unit;

  private final Key key;

  private Context context;

  {
    key = new Key();
    key.setValue("1");
  }

  private final DummyState state;

  {
    state = new DummyState();
    state.setValue("2");
  }
  ;

  private final DummyAction action = new DummyAction();

  private final Class<DummyState> stateType = DummyState.class;
  private final Class<DummyState2> stateType2 = DummyState2.class;
  private final String stateName = stateType.getCanonicalName();
  private final String stateName2 = stateType2.getCanonicalName();
  private final Class<DummyAction> actionType = DummyAction.class;

  @BeforeEach
  public void setUp() {

    context = new Context();

    unit = new PipelineImpl(registry, stateAccess, stateRepository, snapshotter, changeLogger);
  }

  private void verifyNoMoreInteractions() {

    Mockito.verifyNoMoreInteractions(
        registry, stateAccess, stateRepository, reducer, keyStrategy, snapshotter, changeLogger);
  }

  @Test
  public void get() {

    when(registry.getReducer(ReducerKey.create(stateType, RefreshAction.class)))
        .thenThrow(new RuntimeException("can't find"));

    when(stateAccess.get(context, stateType)).thenReturn(Mono.just(state));

    var got = Mono.from(unit.get(context, stateType)).block();
    var expected = new GetResult<DummyState>();

    expected.setState(state);

    assertThat(got).isEqualTo(expected);

    verifyNoMoreInteractions();
  }

  @Test
  public void set() {
    when(stateAccess.get(context, DummyState.class)).thenReturn(Mono.just(state));
    when(registry.getReducer(ReducerKey.create(stateType, actionType))).thenReturn(reducer);
    when(registry.getKeyStrategy(stateType)).thenReturn(keyStrategy);
    when(keyStrategy.apply(context)).thenReturn(Mono.just(key));
    when(reducer.reduce(context, state, action)).thenReturn(Mono.just(state));
    when(snapshotter.snapshot(state)).thenReturn(beforeSnapshot, afterSnapshot);
    when(snapshotter.snapshot(action)).thenReturn(actionSnapshot);
    when(afterSnapshot.diff(beforeSnapshot)).thenReturn(diff);
    when(stateRepository.set(key.getValue(), stateName, state)).thenReturn(Mono.empty());

    var got = Mono.from(unit.set(context, stateType, action)).block();

    var reference = new SetResult<DummyState>();

    reference.setState(state);

    assertThat(got).isEqualTo(reference);

    verify(stateAccess).get(context, DummyState.class);
    verify(registry).getReducer(ReducerKey.create(stateType, actionType));
    verify(registry).getKeyStrategy(stateType);
    verify(keyStrategy).apply(context);
    verify(snapshotter, times(2)).snapshot(state);
    verify(snapshotter).snapshot(action);
    verify(reducer).reduce(context, state, action);
    verify(afterSnapshot).diff(beforeSnapshot);
    verify(stateRepository).set(key.getValue(), stateName, state);
    verify(changeLogger)
        .change(key, stateName, actionType.getCanonicalName(), actionSnapshot, diff);

    verifyNoMoreInteractions();
  }

  @Test
  public void set_ignore() {
    when(stateAccess.get(context, DummyState.class)).thenReturn(Mono.just(state));
    when(registry.getReducer(ReducerKey.create(stateType, actionType))).thenReturn(reducer);
    when(registry.getKeyStrategy(stateType)).thenReturn(keyStrategy);
    when(keyStrategy.apply(context)).thenReturn(Mono.just(key));
    when(snapshotter.snapshot(state)).thenReturn(beforeSnapshot, afterSnapshot);
    when(snapshotter.snapshot(action)).thenReturn(actionSnapshot);
    when(afterSnapshot.diff(beforeSnapshot)).thenReturn(diff);
    when(stateRepository.set(key.getValue(), stateName, state)).thenReturn(Mono.empty());

    when(reducer.reduce(context, state, action)).thenReturn(Mono.error(new IgnoreException(state)));

    var got = Mono.from(unit.set(context, stateType, action)).block();

    var reference = new SetResult<DummyState>();

    reference.setState(state);

    assertThat(got).isEqualTo(reference);

    verify(stateAccess).get(context, DummyState.class);
    verify(registry).getReducer(ReducerKey.create(stateType, actionType));
    verify(registry).getKeyStrategy(stateType);
    verify(keyStrategy).apply(context);
    verify(reducer).reduce(context, state, action);
    verify(afterSnapshot).diff(beforeSnapshot);
    verify(changeLogger)
        .change(key, stateName, actionType.getCanonicalName(), actionSnapshot, diff);
    verify(stateRepository).set(key.getValue(), stateName, state);

    verifyNoMoreInteractions();
  }

  @Test
  public void set_clearState() {
    when(stateAccess.get(context, DummyState.class)).thenReturn(Mono.just(state));
    when(registry.getReducer(ReducerKey.create(stateType, actionType))).thenReturn(reducer);
    when(registry.getKeyStrategy(stateType)).thenReturn(keyStrategy);
    when(keyStrategy.apply(context)).thenReturn(Mono.just(key));
    when(stateRepository.clear(key.getValue(), stateName)).thenReturn(Mono.empty());
    when(snapshotter.snapshot(state)).thenReturn(beforeSnapshot, afterSnapshot);
    when(snapshotter.snapshot(action)).thenReturn(actionSnapshot);

    var expected = new ClearStateException();

    when(reducer.reduce(context, state, action)).thenReturn(Mono.error(expected));

    try {
      Mono.from(unit.set(context, stateType, action)).block();
      fail("Exception should have been thrown");
    } catch (PipelineException e) {
      assertThat(e.getCause()).isSameAs(expected);
    }

    verify(stateAccess).get(context, DummyState.class);
    verify(registry).getReducer(ReducerKey.create(stateType, actionType));
    verify(registry).getKeyStrategy(stateType);
    verify(keyStrategy).apply(context);
    verify(reducer).reduce(context, state, action);
    verify(stateRepository).clear(key.getValue(), stateName);
    verify(changeLogger)
        .change(key, stateName, actionType.getCanonicalName(), actionSnapshot, Diff.CLEAR);

    verifyNoMoreInteractions();
  }

  @Test
  @Disabled
  // TODO - restore later
  public void set_clearAllStates() {
    when(stateAccess.get(context, DummyState.class)).thenReturn(Mono.just(state));
    when(registry.getReducer(ReducerKey.create(stateType, actionType))).thenReturn(reducer);
    when(registry.getKeyStrategy(stateType)).thenReturn(keyStrategy);
    when(keyStrategy.apply(context)).thenReturn(Mono.just(key));
    when(stateRepository.clear(key.getValue(), stateName)).thenReturn(Mono.empty());
    when(stateRepository.clear(key.getValue(), stateName2)).thenReturn(Mono.empty());
    when(snapshotter.snapshot(state)).thenReturn(beforeSnapshot, afterSnapshot);
    when(snapshotter.snapshot(action)).thenReturn(actionSnapshot);
    when(registry.getStateTypes()).thenReturn(Lists.newArrayList(stateType, stateType2));

    var expected = new ClearAllStatesException();

    when(reducer.reduce(context, state, action)).thenReturn(Mono.error(expected));

    try {
      Mono.from(unit.set(context, stateType, action)).block();
      fail("Exception should have been thrown");
    } catch (Exception e) {
      assertThat(e.getCause()).isSameAs(expected);
    }

    verify(stateAccess).get(context, DummyState.class);
    verify(registry).getReducer(ReducerKey.create(stateType, actionType));
    verify(registry).getKeyStrategy(stateType);
    verify(keyStrategy).apply(context);
    verify(reducer).reduce(context, state, action);
    verify(registry).getStateTypes();
    verify(stateRepository).clear(key.getValue(), stateName);
    verify(stateRepository).clear(key.getValue(), stateName2);
    // TODO - May want better info on change of foreign state
    verify(changeLogger)
        .change(key, stateName, actionType.getCanonicalName(), actionSnapshot, Diff.CLEAR);
    verify(changeLogger)
        .change(key, stateName2, actionType.getCanonicalName(), actionSnapshot, Diff.CLEAR);

    verifyNoMoreInteractions();
  }

  @Test
  public void set_clearState_andIgnore() {
    when(stateAccess.get(context, DummyState.class)).thenReturn(Mono.just(state));
    when(registry.getReducer(ReducerKey.create(stateType, actionType))).thenReturn(reducer);
    when(registry.getKeyStrategy(stateType)).thenReturn(keyStrategy);
    when(keyStrategy.apply(context)).thenReturn(Mono.just(key));
    var expected = new ClearStateAndProceedException(state);
    when(reducer.reduce(context, state, action)).thenReturn(Mono.error(expected));
    when(stateRepository.clear(key.getValue(), stateName)).thenReturn(Mono.empty());
    // TODO - Better to zero out the sate for testing here
    when(stateAccess.zero(stateType)).thenReturn(state);
    when(snapshotter.snapshot(state)).thenReturn(beforeSnapshot, afterSnapshot);
    when(snapshotter.snapshot(action)).thenReturn(actionSnapshot);

    var got = Mono.from(unit.set(context, stateType, action)).block();

    assertThat(got.getState()).isSameAs(state);

    verify(stateAccess).get(context, DummyState.class);
    verify(registry).getReducer(ReducerKey.create(stateType, actionType));
    verify(registry).getKeyStrategy(stateType);
    verify(keyStrategy).apply(context);
    verify(reducer).reduce(context, state, action);
    verify(stateRepository).clear(key.getValue(), stateName);
    verify(stateAccess).zero(DummyState.class);
    verify(changeLogger)
        .change(key, stateName, actionType.getCanonicalName(), actionSnapshot, Diff.CLEAR);

    verifyNoMoreInteractions();
  }
}
