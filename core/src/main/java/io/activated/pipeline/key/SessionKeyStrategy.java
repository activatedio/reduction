package io.activated.pipeline.key;

import io.activated.pipeline.Constants;
import io.activated.pipeline.Context;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class SessionKeyStrategy implements KeyStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(SessionKeyStrategy.class);

  @Override
  public Publisher<Key> apply(Context context) {

    return Mono.fromCallable(
            () -> {
              var sessionId = context.getHeaders().get(Constants.SESSION_ID_CONTEXT_KEY);
              if (sessionId == null) {
                throw new IllegalStateException("pipeline-session-id not provided in header");
              }
              var raw = sessionId.get(0);
              if (raw.trim().length() < 16) {
                throw new IllegalStateException("invalid pipeline-session-id");
              }
              /*
              try {
                var got = UUID.fromString(raw);
                if (got.version() != 4) {
                  throw new IllegalStateException("uuid is not v4");
                }
              } catch (Exception e) {
                LOGGER.error("error reading pipeline-session-id", e);
                throw new IllegalStateException("invalid pipeline-session-id");
              }
               */
              return raw;
            })
        .map(
            id -> {
              var key = new Key();
              key.setValue(id);
              return key;
            });
  }
}
