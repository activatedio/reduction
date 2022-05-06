package io.activated.pipeline;

import reactor.core.publisher.Mono;

public interface Pipeline {

  <S> Mono<GetResult<S>> get(Context context, Class<S> stateType);

  <S, A> Mono<SetResult<S>> set(Context context, Class<S> stateType, A action);
}
