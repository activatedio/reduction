package io.activated.pipeline;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public interface BlockingReducer<S, A> extends Reducer<S, A>{

    @Override
    default Publisher<S> reduce(S state, A action) {
        blockingReduce(state, action);
        return Mono.just(state);
    };

    void blockingReduce(S state, A action);
}
