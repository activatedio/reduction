package io.activated.pipeline.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import io.activated.pipeline.PipelineException;
import io.activated.pipeline.StateAccess;
import io.activated.pipeline.StateGuard;
import io.activated.pipeline.fixtures.DummyState;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class GuardedStateAccessTest {

  private GuardedStateAccess unit;

  @Mock private Registry registry;
  @Mock private StateAccess delegate;
  @Mock private StateGuard<DummyState> stateGuard1;
  @Mock private StateGuard<DummyState> stateGuard2;

  private final DummyState state = new DummyState();

  private final PipelineException e = new PipelineException();

  @BeforeEach
  public void setup() {

    unit = new GuardedStateAccess(registry, delegate);
  }

  private void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(registry, delegate, stateGuard1, stateGuard2);
  }

  @Test
  public void get_noStateGuards() {

    when(registry.getStateGuards(DummyState.class)).thenReturn(Lists.emptyList());
    when(delegate.get(DummyState.class)).thenReturn(state);

    assertThat(unit.get(DummyState.class)).isSameAs(state);

    verify(registry).getStateGuards(DummyState.class);
    verify(delegate).get(DummyState.class);

    verifyNoMoreInteractions();
  }

  @Test
  public void get_stateGuards_allPass() {

    when(registry.getStateGuards(DummyState.class))
        .thenReturn(Lists.newArrayList(stateGuard1, stateGuard2));
    when(delegate.get(DummyState.class)).thenReturn(state);

    assertThat(unit.get(DummyState.class)).isSameAs(state);

    verify(registry).getStateGuards(DummyState.class);
    verify(delegate).get(DummyState.class);
    verify(stateGuard1).guardGlobal();
    verify(stateGuard2).guardGlobal();
    verify(stateGuard1).guard(state);
    verify(stateGuard2).guard(state);
    verifyNoMoreInteractions();
  }

  @Test
  public void get_globalGuards_firstFails() {

    when(registry.getStateGuards(DummyState.class))
        .thenReturn(Lists.newArrayList(stateGuard1, stateGuard2));
    doThrow(e).when(stateGuard1).guardGlobal();

    assertThatThrownBy(() -> unit.get(DummyState.class)).isSameAs(e);

    verify(registry).getStateGuards(DummyState.class);
    verify(stateGuard1).guardGlobal();

    verifyNoMoreInteractions();
  }

  @Test
  public void get_globalGuards_secondFails() {

    when(registry.getStateGuards(DummyState.class))
        .thenReturn(Lists.newArrayList(stateGuard1, stateGuard2));
    doThrow(e).when(stateGuard2).guardGlobal();

    assertThatThrownBy(() -> unit.get(DummyState.class)).isSameAs(e);

    verify(registry).getStateGuards(DummyState.class);
    verify(stateGuard1).guardGlobal();
    verify(stateGuard2).guardGlobal();

    verifyNoMoreInteractions();
  }

  @Test
  public void get_stateGuards_firstFails() {

    when(registry.getStateGuards(DummyState.class))
        .thenReturn(Lists.newArrayList(stateGuard1, stateGuard2));
    when(delegate.get(DummyState.class)).thenReturn(state);
    doThrow(e).when(stateGuard1).guard(state);

    assertThatThrownBy(() -> unit.get(DummyState.class)).isSameAs(e);

    verify(registry).getStateGuards(DummyState.class);
    verify(delegate).get(DummyState.class);
    verify(stateGuard1).guardGlobal();
    verify(stateGuard2).guardGlobal();
    verify(stateGuard1).guard(state);

    verifyNoMoreInteractions();
  }

  @Test
  public void get_stateGuards_secondFails() {

    when(registry.getStateGuards(DummyState.class))
        .thenReturn(Lists.newArrayList(stateGuard1, stateGuard2));
    when(delegate.get(DummyState.class)).thenReturn(state);
    doThrow(e).when(stateGuard2).guard(state);

    assertThatThrownBy(() -> unit.get(DummyState.class)).isSameAs(e);

    verify(registry).getStateGuards(DummyState.class);
    verify(delegate).get(DummyState.class);
    verify(stateGuard1).guardGlobal();
    verify(stateGuard2).guardGlobal();
    verify(stateGuard1).guard(state);
    verify(stateGuard2).guard(state);

    verifyNoMoreInteractions();
  }

  @Test
  public void zero() {

    when(delegate.zero(DummyState.class)).thenReturn(state);

    assertThat(unit.zero(DummyState.class)).isSameAs(state);

    verify(delegate).zero(DummyState.class);

    verifyNoMoreInteractions();
  }
}
