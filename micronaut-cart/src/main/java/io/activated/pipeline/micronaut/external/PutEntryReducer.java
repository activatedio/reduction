package io.activated.pipeline.micronaut.external;

import io.activated.pipeline.Context;
import io.activated.pipeline.Reducer;
import io.activated.pipeline.annotations.Operation;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
@Operation
public class PutEntryReducer implements Reducer<InternalState, PutEntryAction> {

  @Override
  public Mono<InternalState> reduce(Context context, InternalState state, PutEntryAction action) {

    return Mono.just(state).doOnNext(_s -> _s.getEntries().put(action.getKey(), action.getValue()));
  }
}
