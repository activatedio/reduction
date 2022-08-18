package io.activated.pipeline;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Lock {

  private final String key;
  private final AtomicInteger nesting;

  public Lock(String key) {
    this.key = key;
    this.nesting = new AtomicInteger(0);
  }

  public String getKey() {
    return key;
  }

  public int getNesting() {
    return nesting.get();
  }

  public void incrementNesting() {
    nesting.incrementAndGet();
  }

  public void decrementNesting() {
    nesting.decrementAndGet();
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
