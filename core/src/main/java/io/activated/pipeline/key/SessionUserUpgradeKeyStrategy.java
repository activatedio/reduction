package io.activated.pipeline.key;

import static io.activated.pipeline.Util.isEmpty;

import io.activated.pipeline.PipelineException;
import io.activated.pipeline.env.PrincipalSupplier;
import io.activated.pipeline.env.SessionIdSupplier;

public class SessionUserUpgradeKeyStrategy implements KeyStrategy {

  private final SessionIdSupplier sessionIdSupplier;
  private final PrincipalSupplier princpalSupplier;

  public SessionUserUpgradeKeyStrategy(
      SessionIdSupplier sessionIdSupplier, PrincipalSupplier princpalSupplier) {
    this.sessionIdSupplier = sessionIdSupplier;
    this.princpalSupplier = princpalSupplier;
  }

  @Override
  public Key get() {

    var sessionId = sessionIdSupplier.get();

    if (isEmpty(sessionId)) {
      throw new PipelineException("Could not obtain key from session");
    }

    String userId = null;

    var principal = princpalSupplier.get();
    if (principal.isPresent()) {
      userId = principal.get().getName();
    }

    var result = new Key();

    if (isEmpty(userId)) {
      result.setValue(sessionId);
    } else {
      result.setValue(String.format("%s_%s", sessionId, userId));
      result.setMoveFrom(sessionId);
    }

    return result;
  }
}
