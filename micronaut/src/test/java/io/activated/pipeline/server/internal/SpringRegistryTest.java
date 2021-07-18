package io.activated.pipeline.server.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.activated.pipeline.InitialState;
import io.activated.pipeline.PipelineException;
import io.activated.pipeline.Reducer;
import io.activated.pipeline.StateGuard;
import io.activated.pipeline.internal.InitialStateKey;
import io.activated.pipeline.internal.ReducerKey;
import io.activated.pipeline.key.KeyStrategy;
import io.activated.pipeline.server.fixtures.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SpringRegistryTest {

  private final ReducerKey reducerKey = ReducerKey.create(Dummy1.class, Dummy2.class);
  private final ReducerKey reducerKeyNotFound = ReducerKey.create(Dummy1.class, Dummy3.class);

  private final InitialStateKey initialStateKey = InitialStateKey.create(Dummy1.class);
  private final InitialStateKey initialStateKeyNotFound = InitialStateKey.create(Dummy2.class);

  private final Set<Class<?>> stateTypes = Sets.newHashSet();

  private final Map<ReducerKey<?, ?>, Class<? extends Reducer<?, ?>>> reducerKeyClassMap =
      Maps.newHashMap();

  private final Map<InitialStateKey<?>, Class<? extends InitialState<?>>> initialStateKeyClassMap =
      Maps.newHashMap();

  private final Map<Class<?>, List<Class<? extends StateGuard<?>>>> stateGuardClassMap =
      Maps.newHashMap();

  private final ReducerClassesSupplier reducerClassesSupplier = () -> reducerKeyClassMap;
  private final StateClassesSupplier stateClassesSupplier = () -> stateTypes;
  private SpringRegistry unit;
  private final InitialStateClassesSupplier initialStateClassesSupplier =
      () -> initialStateKeyClassMap;
  private final StateGuardClassesSupplier stateGuardClassesSupplier = () -> stateGuardClassMap;
  @Mock private ObjectProvider objectProvider;
  @Mock private KeyStrategy defaultKeyStrategy;

  {
    stateTypes.add(Dummy1.class);
    reducerKeyClassMap.put(reducerKey, DummyReducer.class);
    initialStateKeyClassMap.put(initialStateKey, DummyInitialState.class);
    stateGuardClassMap.put(
        DummyState.class, Lists.newArrayList(DummyStateGuard1.class, DummyStateGuard2.class));
  }

  @BeforeEach
  public void setUp() {

    unit =
        new SpringRegistry(
            stateClassesSupplier,
            reducerClassesSupplier,
            initialStateClassesSupplier,
            stateGuardClassesSupplier,
            objectProvider,
            defaultKeyStrategy);
  }

  @Test
  public void getReducer_NotFound() {
    assertThat(catchThrowable(() -> unit.getReducer(reducerKeyNotFound)))
        .isInstanceOf(PipelineException.class)
        .hasMessage("Reducer not registered for key: " + reducerKeyNotFound);
  }

  @Test
  public void getReducer() {
    final var result = new DummyReducer();
    when(objectProvider.get(DummyReducer.class)).thenReturn(result);
    assertThat(unit.getReducer(reducerKey)).isEqualTo(result);
  }

  @Test
  public void getKeyStrategy() {
    assertThat(unit.getKeyStrategy(Dummy1.class)).isEqualTo(defaultKeyStrategy);
  }

  @Test
  public void getStateTypes() {
    assertThat(unit.getStateTypes()).isEqualTo(stateTypes);
  }

  @Test
  public void getReducerKeys() {
    assertThat(unit.getReducerKeys()).isEqualTo(reducerKeyClassMap.keySet());
  }

  @Test
  public void getInitial_NotFound() {
    assertThat(catchThrowable(() -> unit.getInitial(initialStateKeyNotFound)))
        .isInstanceOf(PipelineException.class)
        .hasMessage("InitialState not registered for key: " + initialStateKeyNotFound);
  }

  @Test
  public void getInitial() {
    final var result = new DummyInitialState();
    when(objectProvider.get(DummyInitialState.class)).thenReturn(result);
    assertThat(unit.getInitial(initialStateKey)).isEqualTo(result);
  }

  @Test
  public void getStateGuards() {
    final var result1 = new DummyStateGuard1();
    final var result2 = new DummyStateGuard2();
    when(objectProvider.get(DummyStateGuard1.class)).thenReturn(result1);
    when(objectProvider.get(DummyStateGuard2.class)).thenReturn(result2);
    assertThat(unit.getStateGuards(DummyState.class))
        .isEqualTo(Lists.newArrayList(result1, result2));
  }

  @Test
  public void getStateGuards_notFound() {
    assertThat(unit.getStateGuards(Dummy1.class)).isEmpty();
  }
}
