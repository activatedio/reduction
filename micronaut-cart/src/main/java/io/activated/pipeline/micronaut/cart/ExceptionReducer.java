package io.activated.pipeline.micronaut.cart;

import io.activated.pipeline.Context;
import io.activated.pipeline.Reducer;
import io.activated.pipeline.annotations.Operation;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Operation
@Singleton
public class ExceptionReducer implements Reducer<Cart, ExceptionAction> {

  @Override
  public Mono<Cart> reduce(Context context, Cart state, ExceptionAction action) {
    return Mono.fromCallable(
        () -> {
          throw new IllegalArgumentException("test-exception");
        });
  }
}
