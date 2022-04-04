package io.activated.pipeline.fixtures;

import java.util.Objects;
import javax.validation.constraints.Size;

public class DummyAction {

  @Size(max = 5)
  private String value;

  @Size(max = 2)
  private String value2;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
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
    if (!(o instanceof DummyAction)) return false;
    DummyAction that = (DummyAction) o;
    return Objects.equals(value, that.value) && Objects.equals(value2, that.value2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, value2);
  }
}
