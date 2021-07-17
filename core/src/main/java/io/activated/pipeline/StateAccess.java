package io.activated.pipeline;

public interface StateAccess {
  <S> S get(Class<S> stateType);
}
