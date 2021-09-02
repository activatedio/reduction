package io.activated.pipeline;

import org.reactivestreams.Publisher;

public interface Pipeline {

  <S> GetResult<S> get(Class<S> stateType);

  <S, A> Publisher<SetResult<S>> set(Class<S> stateType, A action);
}
