package io.activated.pipeline.fixtures;

import io.activated.pipeline.Exportable;
import java.util.Objects;

public class InternalState implements Exportable<ExternalState> {

  private String publicValue;
  private String privateValue;

  public String getPublicValue() {
    return publicValue;
  }

  public void setPublicValue(String publicValue) {
    this.publicValue = publicValue;
  }

  public String getPrivateValue() {
    return privateValue;
  }

  public void setPrivateValue(String privateValue) {
    this.privateValue = privateValue;
  }

  @Override
  public ExternalState export() {
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InternalState that = (InternalState) o;
    return Objects.equals(publicValue, that.publicValue)
        && Objects.equals(privateValue, that.privateValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(publicValue, privateValue);
  }

  @Override
  public String toString() {
    return "InternalState{"
        + "publicValue='"
        + publicValue
        + '\''
        + ", privateValue='"
        + privateValue
        + '\''
        + '}';
  }
}
