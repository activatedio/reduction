package io.activated.pipeline.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.activated.pipeline.PipelineConfig;
import io.activated.pipeline.PipelineException;
import io.lettuce.core.api.StatefulRedisConnection;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisStateRepository implements StateRepository {

  private final Logger logger = LoggerFactory.getLogger(RedisStateRepository.class);

  private static final String OK = "OK";

  private final StatefulRedisConnection<String, String> connection;
  private final int ttl;

  private final ObjectMapper objectMapper;

  public RedisStateRepository(
      StatefulRedisConnection<String, String> connection, PipelineConfig config) {
    this.connection = connection;
    this.ttl = config.getStateExpireInSeconds();

    objectMapper = new ObjectMapper();
  }

  @Override
  public boolean exists(String key, String stateName) {
    return connection.sync().exists(makeKey(key, stateName)) > 0;
  }

  @Override
  public void moveKey(String fromKey, String toKey, String stateName) {
    var from = makeKey(fromKey, stateName);
    var to = makeKey(toKey, stateName);

    logger.debug("Moving from key [{}] to key [{}]", from, to);

    checkOk(connection.sync().rename(from, to));
  }

  @Override
  public <S> S get(String key, String stateName, Class<S> targetType) {

    var fullKey = makeKey(key, stateName);

    logger.debug("Getting state for key [{}]", fullKey);

    var resultRaw = connection.sync().get(fullKey);

    if (resultRaw == null) {
      logger.debug("Value for key [{}] is null", fullKey);
      return null;
    }

    logger.debug(
        "Value for key [{}] is not null.  Setting expiry forward {} seconds", fullKey, ttl);

    connection.sync().expire(fullKey, ttl);

    try {
      return objectMapper.readValue(resultRaw, targetType);
    } catch (IOException e) {
      throw new PipelineException(e);
    }
  }

  @Override
  public <S> void set(String key, String stateName, S state) {
    var fullKey = makeKey(key, stateName);
    String json = null;
    try {
      json = objectMapper.writeValueAsString(state);
    } catch (JsonProcessingException e) {
      throw new PipelineException(e);
    }

    logger.debug("Setting value for key [{}] with expiry of [{}]", fullKey, ttl);

    checkOk(connection.sync().setex(fullKey, ttl, json));
  }

  @Override
  public void clear(String key, String stateName) {

    var fullKey = makeKey(key, stateName);
    logger.debug("Clearing value for key [{}]", fullKey);

    connection.sync().del(fullKey);
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
