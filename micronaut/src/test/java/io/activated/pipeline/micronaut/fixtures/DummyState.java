package io.activated.pipeline.micronaut.fixtures;

import io.activated.pipeline.annotations.State;
import java.util.Objects;

@State(guards = {DummyStateGuard1.class, DummyStateGuard2.class})
public class DummyState {

  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final DummyState that = (DummyState) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
