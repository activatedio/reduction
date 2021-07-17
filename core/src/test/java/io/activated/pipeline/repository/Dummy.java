package io.activated.pipeline.repository;

import java.util.Objects;

public class Dummy {

  private String value1;
  private String value2;

  public String getValue1() {
    return value1;
  }

  public void setValue1(String value1) {
    this.value1 = value1;
  }

  public String getValue2() {
    return value2;
  }

  public void setValue2(String value2) {
    this.value2 = value2;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Dummy dummy = (Dummy) o;
    return Objects.equals(value1, dummy.value1) && Objects.equals(value2, dummy.value2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value1, value2);
  }

  @Override
  public String toString() {
    return "Dummy{" + "value1='" + value1 + '\'' + ", value2='" + value2 + '\'' + '}';
  }
}
