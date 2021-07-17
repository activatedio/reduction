package io.activated.pipeline.key;

import java.io.Serializable;
import java.util.Objects;

public class Key implements Serializable {

  private String value;
  private String moveFrom;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getMoveFrom() {
    return moveFrom;
  }

  public void setMoveFrom(String moveFrom) {
    this.moveFrom = moveFrom;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Key key = (Key) o;
    return Objects.equals(value, key.value) && Objects.equals(moveFrom, key.moveFrom);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, moveFrom);
  }

  @Override
  public String toString() {
    return "Key{" + "value='" + value + '\'' + ", moveFrom='" + moveFrom + '\'' + '}';
  }
}
