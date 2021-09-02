package io.activated.pipeline.micronaut.cart;

import io.activated.pipeline.InitialState;
import io.activated.pipeline.annotations.Initial;
import javax.inject.Singleton;

@Initial
@Singleton
public class CartInitialState implements InitialState<Cart> {

  @Override
  public Cart initial() {

    var c = new Cart();
    var a = new Address();
    a.setCity("Test City");

    c.setShippingAddress(a);

    return c;
  }

  @Override
  public Cart zero() {
    return new Cart();
  }
}
