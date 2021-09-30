package io.activated.pipeline.repository;

import static org.assertj.core.api.Assertions.assertThat;

import io.activated.pipeline.PipelineConfig;
import io.activated.pipeline.RandomString;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Optional;

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

    var got = Mono.from(unit.get(sessionId, key, Dummy.class)).block();

    assertThat(got).isEqualTo(Optional.empty());
  }

  @Test
  public void getSetClear() {

    var key = randomString.nextString();
    var name = randomString.nextString();
    var value = new Dummy();

    value.setValue1("value1");
    value.setValue2("value2");

    assertThat(Mono.from(unit.exists(key, name)).block()).isFalse();
    Mono.from(unit.set(key, name, value)).block();
    assertThat(Mono.from(unit.exists(key, name)).block()).isTrue();

    var result = Mono.from(unit.get(key, name, Dummy.class)).block();
    assertThat(result).isEqualTo(Optional.of(value));

    var newKey = key + "-2";

    assertThat(Mono.from(unit.exists(newKey, name)).block()).isFalse();

    Mono.from(unit.moveKey(key, newKey, name)).block();

    assertThat(Mono.from(unit.exists(key, name)).block()).isFalse();
    assertThat(Mono.from(unit.exists(newKey, name)).block()).isTrue();

    result = Mono.from(unit.get(newKey, name, Dummy.class)).block();
    assertThat(result).isEqualTo(Optional.of(value));

    Mono.from(unit.clear(newKey, name)).block();

    assertThat(Mono.from(unit.exists(newKey, name)).block()).isFalse();
  }
}
