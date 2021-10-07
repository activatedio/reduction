package io.activated.pipeline;

import org.reactivestreams.Publisher;

public interface Pipeline {

  <S> Publisher<GetResult<S>> get(Context context, Class<S> stateType);

  <S, A> Publisher<SetResult<S>> set(Context context, Class<S> stateType, A action);
}
