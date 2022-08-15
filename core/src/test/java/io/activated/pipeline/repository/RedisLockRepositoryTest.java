package io.activated.pipeline.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.activated.pipeline.PipelineConfig;
import io.activated.pipeline.PipelineException;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RedisLockRepositoryTest {

  private RedisLockRepository unit;

  @BeforeEach
  public void setUp() {

    RedisClient client = RedisClient.create("redis://localhost");
    StatefulRedisConnection<String, String> connection = client.connect();

    var config = new PipelineConfig();
    config.setSessionLockAcquireSeconds(5);
    config.setSessionLockLengthSeconds(30);

    unit = new RedisLockRepository(connection, config);
  }

  @Test
  public void acquireRelease() {

    var sessionId = UUID.randomUUID().toString();

    var lock = unit.acquire(sessionId);

    assertThat(lock).isNotNull();
    assertThat(lock.getKey()).isSameAs(sessionId);

    assertThatThrownBy(
            () -> {
              unit.acquire(sessionId);
            })
        .isInstanceOf(PipelineException.class)
        .hasMessage("unable to acquire session lock");

    unit.release(lock);

    lock = unit.acquire(sessionId);

    assertThat(lock).isNotNull();
    assertThat(lock.getKey()).isSameAs(sessionId);
  }
}
