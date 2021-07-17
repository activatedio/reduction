package scenarios.model.cart;

import com.google.common.collect.Lists;
import io.activated.pipeline.annotations.State;
import io.activated.pipeline.server.GraphQLResult;
import java.util.List;
import java.util.Objects;

@State(guards = {NoOpStateGuard1.class, NoOpStateGuard2.class})
public class Cart {

  private Address shippingAddress;
  private Address billingAddress;
  private List<CartItem> items = Lists.newArrayList();

  public static class Result extends GraphQLResult<Cart> {};

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

  public List<CartItem> getItems() {
    return items;
  }

  public void setItems(final List<CartItem> items) {
    this.items = items;
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
        && Objects.equals(billingAddress, cart.billingAddress)
        && Objects.equals(items, cart.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shippingAddress, billingAddress, items);
  }

  @Override
  public String toString() {
    return "Cart{"
        + "shippingAddress="
        + shippingAddress
        + ", billingAddress="
        + billingAddress
        + ", items="
        + items
        + '}';
  }
}
