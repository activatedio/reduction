package io.activated.pipeline.micronaut.cart;

import io.activated.pipeline.Constants;
import io.activated.pipeline.Context;
import io.activated.pipeline.Reducer;
import io.activated.pipeline.annotations.Operation;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
@Operation
public class DiagnosticReducer implements Reducer<Cart, DiagnosticAction> {

  @Override
  public Mono<Cart> reduce(Context context, Cart state, DiagnosticAction action) {

    state.setPipelineSessionId(
        (String) context.getAttributes().get(Constants.SESSION_ID_ATTRIBUTE_KEY));

    state.setThreadName(Thread.currentThread().getName());
    return Mono.just(state);
  }
}
