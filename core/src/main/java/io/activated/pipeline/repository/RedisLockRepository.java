package io.activated.pipeline.repository;

import io.activated.pipeline.Lock;
import io.activated.pipeline.PipelineConfig;
import io.activated.pipeline.PipelineException;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisLockRepository implements LockRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(RedisLockRepository.class);

  private final StatefulRedisConnection<String, String> connection;
  private final int sessionLockAcquireMilliseconds;

  private final int sessionLockLengthSeconds;

  public RedisLockRepository(
      StatefulRedisConnection<String, String> connection, PipelineConfig config) {
    this.connection = connection;
    sessionLockAcquireMilliseconds = config.getSessionLockAcquireSeconds() * 1000;
    sessionLockLengthSeconds = config.getSessionLockLengthSeconds();
  }

  @Override
  public Lock acquire(String key) {

    var start = System.currentTimeMillis();

    while ((System.currentTimeMillis() - start) < sessionLockAcquireMilliseconds) {
      var got =
          connection
              .sync()
              .set(
                  makeKey(key),
                  "sentinel",
                  SetArgs.Builder.ex(Duration.ofSeconds(sessionLockLengthSeconds)).nx());

      if ("OK".equals(got)) {

        LOGGER.info("acquired lock for sessionId: {}", key);

        return new Lock(key);
      }

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    throw new PipelineException("unable to acquire session lock");
  }

  public void release(Lock lock) {
    connection.sync().del(makeKey(lock.getKey()));
  }

  private String makeKey(String input) {
    return "Pipline.sessionLock-" + input;
  }
}
