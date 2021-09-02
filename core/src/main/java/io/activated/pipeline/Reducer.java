package io.activated.pipeline;

import io.reactivex.Maybe;
import org.reactivestreams.Publisher;

public interface Reducer<S, A> {

    Publisher<S> reduce(S state, A action);
}
