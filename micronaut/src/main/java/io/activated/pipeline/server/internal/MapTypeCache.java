package io.activated.pipeline.server.internal;

import io.activated.pipeline.server.TypeCache;
import java.util.HashMap;
import java.util.Map;

public class MapTypeCache<T> implements TypeCache<T> {

  private final Map<Class<?>, T> cache = new HashMap<Class<?>, T>();

  @Override
  public T get(final Class<?> input) {
    return cache.get(input);
  }

  @Override
  public void put(final Class<?> input, final T type) {
    cache.put(input, type);
  }
}
