package io.activated.pipeline.micronaut.fixtures;

import java.util.Objects;

public class DummyExternalState {

  private String externalValue;

  public String getExternalValue() {
    return externalValue;
  }

  public void setExternalValue(String externalValue) {
    this.externalValue = externalValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DummyExternalState that = (DummyExternalState) o;
    return Objects.equals(externalValue, that.externalValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(externalValue);
  }

  @Override
  public String toString() {
    return "DummyExternalState{" + "externalValue='" + externalValue + '\'' + '}';
  }
}
