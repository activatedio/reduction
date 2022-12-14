package io.activated.pipeline.micronaut.cart;

import java.util.Objects;

public class SetAddressBlocking {

  private String addressType;
  private Address address;

  public String getAddressType() {
    return addressType;
  }

  public void setAddressType(final String addressType) {
    this.addressType = addressType;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(final Address address) {
    this.address = address;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final SetAddressBlocking that = (SetAddressBlocking) o;
    return Objects.equals(addressType, that.addressType) && Objects.equals(address, that.address);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addressType, address);
  }

  @Override
  public String toString() {
    return "SetAddress{" + "addressType='" + addressType + '\'' + ", address=" + address + '}';
  }
}
