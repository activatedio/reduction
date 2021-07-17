package io.activated.pipeline;

public interface Pipeline {

  <S> GetResult<S> get(Class<S> stateType);

  <S, A> SetResult<S> set(Class<S> stateType, A action);
}
