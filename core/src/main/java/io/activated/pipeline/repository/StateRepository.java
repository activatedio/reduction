package io.activated.pipeline.repository;

public interface StateRepository {

  boolean exists(String key, String stateName);

  void moveKey(String fromKey, String toKey, String stateName);

  <S> S get(String key, String stateName, Class<S> targetType);

  <S> void set(String key, String stateName, S state);

  void clear(String key, String stateName);
}
