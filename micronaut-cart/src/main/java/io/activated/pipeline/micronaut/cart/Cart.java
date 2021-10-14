package io.activated.pipeline.micronaut.cart;

import com.google.common.collect.Lists;
import io.activated.pipeline.annotations.State;
import java.util.List;
import java.util.Objects;

@State(guards = {NoOpStateGuard1.class, NoOpStateGuard2.class})
public class Cart {

  private int count = 1;
  private Address shippingAddress;
  private Address billingAddress;
  private List<CartItem> cartItems = Lists.newArrayList();
  private String threadName;
  private String pipelineSessionIdUppercase;
  private String pipelineSessionIdLowercase;

  public Cart() {}

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public void incrementCount() {
    count++;
  }

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

  public String getThreadName() {
    return threadName;
  }

  public void setThreadName(String threadName) {
    this.threadName = threadName;
  }

  public String getPipelineSessionIdUppercase() {
    return pipelineSessionIdUppercase;
  }

  public void setPipelineSessionIdUppercase(String pipelineSessionIdUppercase) {
    this.pipelineSessionIdUppercase = pipelineSessionIdUppercase;
  }

  public String getPipelineSessionIdLowercase() {
    return pipelineSessionIdLowercase;
  }

  public void setPipelineSessionIdLowercase(String pipelineSessionIdLowercase) {
    this.pipelineSessionIdLowercase = pipelineSessionIdLowercase;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Cart)) return false;
    Cart cart = (Cart) o;
    return count == cart.count
        && Objects.equals(shippingAddress, cart.shippingAddress)
        && Objects.equals(billingAddress, cart.billingAddress)
        && Objects.equals(cartItems, cart.cartItems)
        && Objects.equals(threadName, cart.threadName)
        && Objects.equals(pipelineSessionIdUppercase, cart.pipelineSessionIdUppercase)
        && Objects.equals(pipelineSessionIdLowercase, cart.pipelineSessionIdLowercase);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        count,
        shippingAddress,
        billingAddress,
        cartItems,
        threadName,
        pipelineSessionIdUppercase,
        pipelineSessionIdLowercase);
  }

  @Override
  public String toString() {
    return "Cart{"
        + "count="
        + count
        + ", shippingAddress="
        + shippingAddress
        + ", billingAddress="
        + billingAddress
        + ", cartItems="
        + cartItems
        + ", threadName='"
        + threadName
        + '\''
        + ", pipelineSessionIdUppercase='"
        + pipelineSessionIdUppercase
        + '\''
        + ", pipelineSessionIdLowercase='"
        + pipelineSessionIdLowercase
        + '\''
        + '}';
  }
}
