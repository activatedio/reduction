package io.activated.pipeline.key;

import io.activated.pipeline.Constants;
import io.activated.pipeline.PipelineException;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public class SessionKeyStrategy implements KeyStrategy {

  @Override
  public Publisher<Key> get() {

    return Mono.deferWithContext(
            ctx -> {
              String keyValue = ctx.get(Constants.SESSION_ID_CONTEXT_KEY);
              return Mono.just(keyValue);
            })
        .map(
            s -> {
              var key = new Key();
              key.setValue(s);
              return key;
            })
        .switchIfEmpty(Mono.error(new PipelineException("Could not obtain key from session")));
  }
}
