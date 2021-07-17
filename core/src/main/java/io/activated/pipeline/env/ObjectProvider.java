package io.activated.pipeline.env;

public interface ObjectProvider {

  <T> T get(Class<T> type);
}
