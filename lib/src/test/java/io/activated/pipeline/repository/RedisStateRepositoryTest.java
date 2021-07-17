package io.activated.pipeline.repository;

import static org.assertj.core.api.Assertions.assertThat;

import io.activated.pipeline.PipelineConfig;
import io.activated.pipeline.RandomString;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RedisStateRepositoryTest {

  private RedisStateRepository unit;
  private final RandomString randomString = new RandomString();

  @BeforeEach
  public void setUp() {

    RedisClient client = RedisClient.create("redis://localhost");
    StatefulRedisConnection<String, String> connection = client.connect();

    var config = new PipelineConfig();
    config.setStateExpireInSeconds(300);

    unit = new RedisStateRepository(connection, config);
  }

  @Test
  public void get_Initial() {
    var sessionId = randomString.nextString();
    var key = randomString.nextString();

    Dummy got = unit.get(sessionId, key, Dummy.class);

    assertThat(got).isNull();
  }

  @Test
  public void getSetClear() {

    var key = randomString.nextString();
    var name = randomString.nextString();
    var value = new Dummy();

    value.setValue1("value1");
    value.setValue2("value2");

    assertThat(unit.exists(key, name)).isFalse();
    unit.set(key, name, value);
    assertThat(unit.exists(key, name)).isTrue();

    var result = unit.get(key, name, Dummy.class);
    assertThat(result).isEqualTo(value);

    var newKey = key + "-2";

    assertThat(unit.exists(newKey, name)).isFalse();

    unit.moveKey(key, newKey, name);

    assertThat(unit.exists(key, name)).isFalse();
    assertThat(unit.exists(newKey, name)).isTrue();

    result = unit.get(newKey, name, Dummy.class);
    assertThat(result).isEqualTo(value);

    unit.clear(newKey, name);

    assertThat(unit.exists(newKey, name)).isFalse();
  }
}
