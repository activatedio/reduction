package io.activated.pipeline.micronaut.cart;

import com.google.common.collect.Lists;
import io.activated.pipeline.annotations.State;

import java.util.List;
import java.util.Objects;

@State(guards = {NoOpStateGuard1.class, NoOpStateGuard2.class})
public class Cart {

  private Address shippingAddress;
  private Address billingAddress;
  private List<CartItem> cartItems = Lists.newArrayList();

  public Cart() {}

  public Address getShippingAddress() {
    return shippingAddress;
  }

  public void setShippingAddress(final Address shippingAddress) {
    this.shippingAddress = shippingAddress;
  }

  public Address getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(final Address billingAddress) {
    this.billingAddress = billingAddress;
  }

  public List<CartItem> getCartItems() {
    return cartItems;
  }

  public void setCartItems(List<CartItem> cartItems) {
    this.cartItems = cartItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Cart cart = (Cart) o;
    return Objects.equals(shippingAddress, cart.shippingAddress) && Objects.equals(billingAddress, cart.billingAddress) && Objects.equals(cartItems, cart.cartItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shippingAddress, billingAddress, cartItems);
  }

  @Override
  public String toString() {
    return "Cart{" +
            "shippingAddress=" + shippingAddress +
            ", billingAddress=" + billingAddress +
            ", cartItems=" + cartItems +
            '}';
  }
}
