package io.activated.pipeline.server.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class MapTypeCacheTest {

  public static class Key1 {}

  public static class Key2 {}

  @Test
  public void putGet() {
    var unit = new MapTypeCache<String>();

    assertThat(unit.get(Key1.class)).isNull();
    assertThat(unit.get(Key2.class)).isNull();

    unit.put(Key1.class, "1");
    unit.put(Key2.class, "2");

    assertThat(unit.get(Key1.class)).isEqualTo("1");
    assertThat(unit.get(Key2.class)).isEqualTo("2");
  }
}
