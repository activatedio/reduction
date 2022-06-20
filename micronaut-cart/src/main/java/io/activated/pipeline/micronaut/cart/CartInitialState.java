package io.activated.pipeline.micronaut.cart;

import io.activated.pipeline.Context;
import io.activated.pipeline.InitialState;
import io.activated.pipeline.annotations.Initial;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Initial
@Singleton
public class CartInitialState implements InitialState<Cart> {

  @Override
  public Mono<Cart> initial(Context context) {

    var c = new Cart();
    var a = new Address();
    a.setCity("Test City");

    c.setShippingAddress(a);

    return Mono.just(c);
  }

  @Override
  public Cart zero() {
    return new Cart();
  }
}
