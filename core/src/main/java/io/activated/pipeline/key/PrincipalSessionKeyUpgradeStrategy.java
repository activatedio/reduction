package io.activated.pipeline.key;

import io.activated.pipeline.Context;
import reactor.core.publisher.Mono;

public class PrincipalSessionKeyUpgradeStrategy implements KeyStrategy {

  private final SessionKeyStrategy sessionKeyStrategy;

  public PrincipalSessionKeyUpgradeStrategy(SessionKeyStrategy sessionKeyStrategy) {
    this.sessionKeyStrategy = sessionKeyStrategy;
  }

  @Override
  public Mono<Key> apply(Context context) {
    return Mono.from(sessionKeyStrategy.apply(context))
        .map(
            k -> {
              if (context.getPrincipalWrapper() != null) {
                var key = new Key();
                key.setValue(
                    String.format("%s-%s", k.getValue(), context.getPrincipalWrapper().getSub()));
                key.setMoveFrom(k.getValue());
                return key;
              } else {
                return k;
              }
            });
  }
}
