package io.activated.pipeline.micronaut.external;

import io.activated.pipeline.Exportable;
import io.activated.pipeline.annotations.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@State
public class InternalState implements Exportable<ExternalState> {

  private Map<String, String> entries = new HashMap<>();

  public Map<String, String> getEntries() {
    return entries;
  }

  public void setEntries(Map<String, String> entries) {
    this.entries = entries;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InternalState that = (InternalState) o;
    return Objects.equals(entries, that.entries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entries);
  }

  @Override
  public String toString() {
    return "InternalState{" + "entries=" + entries + '}';
  }

  @Override
  public ExternalState export() {

    var result = new ExternalState();

    result.setKeys(new ArrayList<>(entries.keySet()));

    return result;
  }
}
