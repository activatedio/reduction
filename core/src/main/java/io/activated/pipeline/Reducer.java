package io.activated.pipeline;

import reactor.core.publisher.Mono;

public interface Reducer<S, A> {

  Mono<S> reduce(Context context, S state, A action);
}
