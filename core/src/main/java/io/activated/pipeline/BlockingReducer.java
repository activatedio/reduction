package io.activated.pipeline;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public interface BlockingReducer<S, A> extends Reducer<S, A> {

  @Override
  default Publisher<S> reduce(Context context, S state, A action) {
    blockingReduce(context, state, action);
    return Mono.just(state);
  }
  ;

  default void blockingReduce(Context context, S state, A action) {
    blockingReduce(state, action);
  }
  ;

  default void blockingReduce(S state, A action) {
    throw new UnsupportedOperationException();
  }
  ;
}
