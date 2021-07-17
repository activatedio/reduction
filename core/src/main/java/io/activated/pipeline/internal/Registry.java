package io.activated.pipeline.internal;

import io.activated.pipeline.InitialState;
import io.activated.pipeline.Reducer;
import io.activated.pipeline.StateGuard;
import io.activated.pipeline.key.KeyStrategy;
import java.util.List;

public interface Registry {

  <S, A> Reducer<S, A> getReducer(ReducerKey<S, A> key);

  <S> KeyStrategy getKeyStrategy(Class<S> stateType);

  <S> List<StateGuard<S>> getStateGuards(Class<S> stateType);

  Iterable<Class<?>> getStateTypes();

  Iterable<ReducerKey<?, ?>> getReducerKeys();

  <S> InitialState<S> getInitial(InitialStateKey<S> key);
}
