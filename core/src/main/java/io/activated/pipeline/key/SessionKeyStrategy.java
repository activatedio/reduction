package io.activated.pipeline.key;

import io.activated.pipeline.Constants;
import io.activated.pipeline.Context;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public class SessionKeyStrategy implements KeyStrategy {

  @Override
  public Publisher<Key> apply(Context context) {

    return Mono.fromCallable(
            () -> {
              var sessionId = context.getHeaders().get(Constants.SESSION_ID_CONTEXT_KEY);
              if (sessionId == null) {
                throw new IllegalStateException("pipeline-session-id not provided in header");
              }
              return sessionId.get(0);
            })
        .map(
            id -> {
              var key = new Key();
              key.setValue(id);
              return key;
            });
  }
}
