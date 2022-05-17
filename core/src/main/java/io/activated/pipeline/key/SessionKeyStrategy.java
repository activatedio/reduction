package io.activated.pipeline.key;

import io.activated.pipeline.Constants;
import io.activated.pipeline.Context;
import java.util.Objects;
import reactor.core.publisher.Mono;

public class SessionKeyStrategy implements KeyStrategy {

  @Override
  public Mono<Key> apply(Context context) {

    return Mono.fromCallable(
            () ->
                Objects.requireNonNull(
                    (String) context.getAttributes().get(Constants.SESSION_ID_ATTRIBUTE_KEY),
                    Constants.SESSION_ID_ATTRIBUTE_KEY))
        .map(
            id -> {
              var key = new Key();
              key.setValue(id);
              return key;
            });
  }
}
