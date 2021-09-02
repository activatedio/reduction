package io.activated.pipeline;

import io.reactivex.internal.operators.flowable.FlowableSingle;
import org.reactivestreams.Publisher;

public interface BlockingReducer<S, A> extends Reducer<S, A>{

    @Override
    default Publisher<S> reduce(S state, A action) {
        blockingReduce(state, action);
        return FlowableSingle.just(state);
    };

    void blockingReduce(S state, A action);
}
