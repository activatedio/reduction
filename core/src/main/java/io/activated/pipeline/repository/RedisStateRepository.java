package io.activated.pipeline.repository;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import io.activated.pipeline.PipelineConfig;
import io.activated.pipeline.PipelineException;
import io.lettuce.core.api.StatefulRedisConnection;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class RedisStateRepository implements StateRepository {

  private final Logger logger = LoggerFactory.getLogger(RedisStateRepository.class);

  private static class WithKey {
    private final String key;
    private final String content;

    private WithKey(String key, String content) {
      this.key = key;
      this.content = content;
    }
  }

  private static final String OK = "OK";

  private final StatefulRedisConnection<String, String> connection;
  private final int ttl;

  private final ObjectMapper objectMapper;

  public RedisStateRepository(
      StatefulRedisConnection<String, String> connection, PipelineConfig config) {
    this.connection = connection;
    this.ttl = config.getStateExpireSeconds();

    objectMapper =
        new ObjectMapper()
            .activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_OBJECT)
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    objectMapper.setVisibility(
        objectMapper
            .getSerializationConfig()
            .getDefaultVisibilityChecker()
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
  }

  @Override
  public Mono<Boolean> exists(String key, String stateName) {
    return connection.reactive().exists(makeKey(key, stateName)).map(l -> l > 0);
  }

  @Override
  public Mono<Void> moveKey(String fromKey, String toKey, String stateName) {
    var from = makeKey(fromKey, stateName);
    var to = makeKey(toKey, stateName);

    logger.debug("Moving from key [{}] to key [{}]", from, to);

    return connection.reactive().rename(from, to).doOnNext(this::checkOk).then();
  }

  @Override
  public <S> Mono<Optional<S>> get(String key, String stateName, Class<S> targetType) {

    return Mono.fromCallable(() -> makeKey(key, stateName))
        .doOnNext(s -> logger.debug("Getting state for key [{}]", s))
        .flatMap(fullKey -> connection.reactive().get(fullKey).map(s -> new WithKey(fullKey, s)))
        .doOnNext(
            s ->
                logger.debug(
                    "Value for key [{}] is not null.  Setting expiry forward {} seconds", s, ttl))
        .flatMap(wk -> connection.reactive().expire(wk.key, ttl).map(r -> wk.content))
        .map(
            r -> {
              try {
                return objectMapper.readValue(r, targetType);
              } catch (IOException e) {
                throw new PipelineException(e);
              }
            })
        .map(Optional::of)
        .defaultIfEmpty(Optional.empty());
  }

  @Override
  public <S> Mono<Void> set(String key, String stateName, S state) {

    return Mono.fromCallable(() -> makeKey(key, stateName))
        .map(
            k -> {
              try {
                var content = objectMapper.writeValueAsString(state);
                return new WithKey(k, content);
              } catch (JsonProcessingException e) {
                throw new PipelineException(e);
              }
            })
        .doOnNext(
            wk ->
                logger.debug(
                    "Setting content [{}] for key [{}] with expiry of [{}]",
                    wk.content,
                    wk.key,
                    ttl))
        .flatMap(wk -> connection.reactive().setex(wk.key, ttl, wk.content))
        .doOnNext(this::checkOk)
        .then();
  }

  @Override
  public Mono<Void> clear(String key, String stateName) {

    return Mono.fromCallable(() -> makeKey(key, stateName))
        .flatMap(fullKey -> connection.reactive().del(fullKey))
        .doOnNext(fullKey -> logger.debug("Clearing value for key [{}]", fullKey))
        .then();
  }

  private String makeKey(String sessionId, String stateName) {
    return String.format("pieline-state_%s_%s", sessionId, stateName);
  }

  private void checkOk(String value) {
    if (!OK.equals(value)) {
      throw new PipelineException("Non-OK return code: " + value);
    }
  }
}
