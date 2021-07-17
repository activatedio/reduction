package scenarios.model.cart;

import java.util.Objects;

public class Address {

  private String name;
  private String street;
  private String city;
  private String state;
  private String zip;

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(final String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(final String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(final String state) {
    this.state = state;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(final String zip) {
    this.zip = zip;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Address address = (Address) o;
    return Objects.equals(name, address.name)
        && Objects.equals(street, address.street)
        && Objects.equals(city, address.city)
        && Objects.equals(state, address.state)
        && Objects.equals(zip, address.zip);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, street, city, state, zip);
  }

  @Override
  public String toString() {
    return "Address{"
        + "name='"
        + name
        + '\''
        + ", street='"
        + street
        + '\''
        + ", city='"
        + city
        + '\''
        + ", state='"
        + state
        + '\''
        + ", zip='"
        + zip
        + '\''
        + '}';
  }
}
