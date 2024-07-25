package io.activated.pipeline;

import java.io.Serializable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Created by btomasini on 4/25/17. */
public abstract class JUnit5ModelTestSupport<T> {

  protected abstract T makeReference();

  /** Can modify in place or return new one */
  protected abstract T modifyReference(T input);

  @Test
  public void equals_same() {

    final T obj = makeReference();
    Assertions.assertTrue(obj.equals(obj));
  }

  @Test
  public void equals_equals() {

    final T obj1 = makeReference();
    final T obj2 = makeReference();
    Assertions.assertTrue(obj1.equals(obj2));
  }

  @Test
  public void equals_notEquals() {

    final T obj1 = makeReference();
    final T obj2 = modifyReference(makeReference());
    Assertions.assertFalse(obj1.equals(obj2));
  }

  @Test
  public void equals_null() {

    final T obj = makeReference();
    Assertions.assertFalse(obj.equals(null));
  }

  @Test
  public void hashCode_same() {

    final T obj = makeReference();
    Assertions.assertTrue(obj.equals(obj));
  }

  @Test
  public void hashCode_equals() {

    final T obj1 = makeReference();
    final T obj2 = makeReference();
    Assertions.assertEquals(obj1.hashCode(), obj2.hashCode());
  }

  @Test
  public void hashCode_notEquals() {

    final T obj1 = makeReference();
    final T obj2 = modifyReference(makeReference());
    Assertions.assertNotEquals(obj1.hashCode(), obj2.hashCode());
  }

  @Test
  public void implementsSerializable() {

    final T obj = makeReference();
    Assertions.assertTrue(Serializable.class.isAssignableFrom(obj.getClass()));
  }

  @Test
  public void nonDefaultToString() {

    final T obj = makeReference();
    Assertions.assertNotEquals(obj.toString(), defaultToString(obj));
  }

  private String defaultToString(final Object obj) {

    return String.format(
        "%s@%s", obj.getClass().getCanonicalName(), Integer.toHexString(obj.hashCode()));
  }
}
