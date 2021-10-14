package io.activated.pipeline.micronaut.cart;

import io.activated.pipeline.Constants;
import io.activated.pipeline.Context;
import io.activated.pipeline.Reducer;
import io.activated.pipeline.annotations.Operation;
import javax.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Singleton
@Operation
public class DiagnosticReducer implements Reducer<Cart, DiagnosticAction> {

  @Override
  public Publisher<Cart> reduce(Context context, Cart state, DiagnosticAction action) {

    state.setPipelineSessionIdLowercase(
        context.getHeaders().get(Constants.SESSION_ID_CONTEXT_KEY.toLowerCase()).get(0));
    state.setPipelineSessionIdUppercase(
        context.getHeaders().get(Constants.SESSION_ID_CONTEXT_KEY.toUpperCase()).get(0));

    state.setThreadName(Thread.currentThread().getName());
    return Mono.just(state);
  }
}
