package io.activated.pipeline.micronaut.external;

import java.util.List;
import java.util.Objects;

public class ExternalState {

  private List<String> keys;

  public List<String> getKeys() {
    return keys;
  }

  public void setKeys(List<String> keys) {
    this.keys = keys;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExternalState that = (ExternalState) o;
    return Objects.equals(keys, that.keys);
  }

  @Override
  public int hashCode() {
    return Objects.hash(keys);
  }

  @Override
  public String toString() {
    return "ExternalState{" + "keys=" + keys + '}';
  }
}
