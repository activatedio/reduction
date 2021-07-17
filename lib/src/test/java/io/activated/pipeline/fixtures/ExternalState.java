package io.activated.pipeline.fixtures;

import java.util.Objects;

public class ExternalState {

  private String publicValue;

  public String getPublicValue() {
    return publicValue;
  }

  public void setPublicValue(String publicValue) {
    this.publicValue = publicValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExternalState that = (ExternalState) o;
    return Objects.equals(publicValue, that.publicValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(publicValue);
  }

  @Override
  public String toString() {
    return "ExternalState{" + "publicValue='" + publicValue + '\'' + '}';
  }
}
