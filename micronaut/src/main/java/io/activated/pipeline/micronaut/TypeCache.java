package io.activated.pipeline.micronaut;

public interface TypeCache<T> {

  T get(Class<?> input);

  void put(Class<?> input, T type);
}
