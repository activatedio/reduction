package io.activated.pipeline.fixtures;

import java.util.Objects;

public class DummyState2 {

  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DummyState2 that = (DummyState2) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "DummyState2{" + "value='" + value + '\'' + '}';
  }
}
