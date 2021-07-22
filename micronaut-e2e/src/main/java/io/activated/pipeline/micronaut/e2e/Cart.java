package io.activated.pipeline.micronaut.e2e;

import io.activated.pipeline.annotations.State;

import java.util.Objects;

@State(guards = {NoOpStateGuard1.class, NoOpStateGuard2.class})
public class Cart {

  private Address shippingAddress;
  private Address billingAddress;

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

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Cart cart = (Cart) o;
    return Objects.equals(shippingAddress, cart.shippingAddress)
        && Objects.equals(billingAddress, cart.billingAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shippingAddress, billingAddress);
  }

  @Override
  public String toString() {
    return "Cart{"
        + "shippingAddress="
        + shippingAddress
        + ", billingAddress="
        + billingAddress
        + '}';
  }
}
