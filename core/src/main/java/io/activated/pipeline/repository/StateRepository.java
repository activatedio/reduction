package io.activated.pipeline.repository;

import java.util.Optional;
import org.reactivestreams.Publisher;

public interface StateRepository {

  Publisher<Boolean> exists(String key, String stateName);

  Publisher<Void> moveKey(String fromKey, String toKey, String stateName);

  <S> Publisher<Optional<S>> get(String key, String stateName, Class<S> targetType);

  <S> Publisher<Void> set(String key, String stateName, S state);

  Publisher<Void> clear(String key, String stateName);
}
