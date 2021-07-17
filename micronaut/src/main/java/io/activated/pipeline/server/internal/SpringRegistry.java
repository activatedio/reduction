package io.activated.pipeline.server.internal;

import io.activated.pipeline.InitialState;
import io.activated.pipeline.PipelineException;
import io.activated.pipeline.Reducer;
import io.activated.pipeline.StateGuard;
import io.activated.pipeline.env.ObjectProvider;
import io.activated.pipeline.internal.InitialStateKey;
import io.activated.pipeline.internal.ReducerKey;
import io.activated.pipeline.internal.Registry;
import io.activated.pipeline.key.KeyStrategy;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpringRegistry implements Registry {

  private final StateClassesSupplier stateClassesSupplier;
  private final ReducerClassesSupplier reducerClassesSupplier;
  private final InitialStateClassesSupplier initialStateClassesSupplier;
  private final StateGuardClassesSupplier stateGuardsSupplier;
  private final ObjectProvider objectProvider;
  private final KeyStrategy defaultKeyStrategy;

  @Autowired
  public SpringRegistry(
      final StateClassesSupplier stateClassesSupplier,
      final ReducerClassesSupplier reducerClassesSupplier,
      final InitialStateClassesSupplier initialStateClassesSupplier,
      StateGuardClassesSupplier stateGuardsSupplier,
      final ObjectProvider objectProvider,
      final KeyStrategy defaultKeyStrategy) {
    this.stateClassesSupplier = stateClassesSupplier;
    this.reducerClassesSupplier = reducerClassesSupplier;
    this.initialStateClassesSupplier = initialStateClassesSupplier;
    this.stateGuardsSupplier = stateGuardsSupplier;
    this.objectProvider = objectProvider;
    this.defaultKeyStrategy = defaultKeyStrategy;
  }

  @Override
  public <S, A> Reducer<S, A> getReducer(final ReducerKey<S, A> key) {
    final var cls = reducerClassesSupplier.get().get(key);
    if (cls == null) {
      throw new PipelineException("Reducer not registered for key: " + key);
    }
    return (Reducer<S, A>) objectProvider.get(cls);
  }

  @Override
  public <S> KeyStrategy getKeyStrategy(final Class<S> stateType) {
    return defaultKeyStrategy;
  }

  @Override
  public <S> List<StateGuard<S>> getStateGuards(Class<S> stateType) {
    List<StateGuard<S>> result = new ArrayList<>();

    var clss = stateGuardsSupplier.get().get(stateType);
    if (clss == null) {
      return result;
    }
    for (var cls : clss) {
      result.add((StateGuard<S>) objectProvider.get(cls));
    }

    return result;
  }

  @Override
  public Iterable<Class<?>> getStateTypes() {
    return stateClassesSupplier.get();
  }

  @Override
  public Iterable<ReducerKey<?, ?>> getReducerKeys() {
    return reducerClassesSupplier.get().keySet();
  }

  @Override
  public <S> InitialState<S> getInitial(final InitialStateKey<S> key) {
    final var cls = initialStateClassesSupplier.get().get(key);
    if (cls == null) {
      throw new PipelineException("InitialState not registered for key: " + key);
    }
    return (InitialState<S>) objectProvider.get(cls);
  }
}
