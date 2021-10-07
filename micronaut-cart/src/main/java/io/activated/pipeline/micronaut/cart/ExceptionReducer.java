package io.activated.pipeline.micronaut.cart;

import io.activated.pipeline.Reducer;
import io.activated.pipeline.annotations.Operation;
import javax.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Operation
@Singleton
public class ExceptionReducer implements Reducer<Cart, ExceptionAction> {

  @Override
  public Publisher<Cart> reduce(Cart state, ExceptionAction action) {
    return Mono.fromCallable(
        () -> {
          throw new IllegalArgumentException("test-exception");
        });
  }
}
