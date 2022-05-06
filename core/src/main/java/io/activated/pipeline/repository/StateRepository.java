package io.activated.pipeline.repository;

import java.util.Optional;
import reactor.core.publisher.Mono;

public interface StateRepository {

  Mono<Boolean> exists(String key, String stateName);

  Mono<Void> moveKey(String fromKey, String toKey, String stateName);

  <S> Mono<Optional<S>> get(String key, String stateName, Class<S> targetType);

  <S> Mono<Void> set(String key, String stateName, S state);

  Mono<Void> clear(String key, String stateName);
}
