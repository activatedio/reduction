package io.activated.pipeline;

import java.io.Serializable;
import java.util.Objects;

public class SetResult<S> implements Serializable {
  private S state;

  public S getState() {
    return state;
  }

  public void setState(S state) {
    this.state = state;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SetResult<?> setResult = (SetResult<?>) o;
    return Objects.equals(state, setResult.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(state);
  }

  @Override
  public String toString() {
    return "SetResult{" + "state=" + state + '}';
  }
}
