package io.activated.pipeline;

import java.util.Objects;

public class Lock {

  private final String key;

  public Lock(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Lock lock = (Lock) o;
    return Objects.equals(key, lock.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }

  @Override
  public String toString() {
    return "Lock{" + "key='" + key + '\'' + '}';
  }
}
