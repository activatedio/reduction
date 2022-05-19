package io.activated.pipeline.micronaut.fixtures;

import io.activated.pipeline.Exportable;
import java.util.Objects;

public class DummyInternalState implements Exportable<DummyExternalState> {

  private String internalValue;

  public String getInternalValue() {
    return internalValue;
  }

  public void setInternalValue(String internalValue) {
    this.internalValue = internalValue;
  }

  @Override
  public DummyExternalState export() {

    var state = new DummyExternalState();
    state.setExternalValue(internalValue);
    return state;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DummyInternalState that = (DummyInternalState) o;
    return Objects.equals(internalValue, that.internalValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(internalValue);
  }

  @Override
  public String toString() {
    return "DummyInternalState{" + "internalValue='" + internalValue + '\'' + '}';
  }
}
