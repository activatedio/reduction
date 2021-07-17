package io.activated.pipeline.server;

public interface TypeCache<T> {

  T get(Class<?> input);

  void put(Class<?> input, T type);
}
