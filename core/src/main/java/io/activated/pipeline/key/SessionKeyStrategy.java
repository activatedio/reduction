package io.activated.pipeline.key;

import static io.activated.pipeline.Util.isEmpty;

import io.activated.pipeline.PipelineException;
import io.activated.pipeline.env.SessionIdSupplier;

public class SessionKeyStrategy implements KeyStrategy {

  private final SessionIdSupplier sessionIdSupplier;

  public SessionKeyStrategy(SessionIdSupplier sessionIdSupplier) {
    this.sessionIdSupplier = sessionIdSupplier;
  }

  @Override
  public Key get() {

    var key = sessionIdSupplier.get();

    // TODO - Is empty method somewhere?
    if (isEmpty(key)) {
      throw new PipelineException("Could not obtain key from session");
    }

    var result = new Key();
    result.setValue(key);

    return result;
  }
}
