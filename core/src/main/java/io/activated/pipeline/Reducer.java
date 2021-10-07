package io.activated.pipeline;

import org.reactivestreams.Publisher;

public interface Reducer<S, A> {

  Publisher<S> reduce(Context context, S state, A action);
}
