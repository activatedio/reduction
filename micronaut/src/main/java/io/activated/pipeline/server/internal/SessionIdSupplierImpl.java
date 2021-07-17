package io.activated.pipeline.server.internal;

import io.activated.pipeline.env.SessionIdSupplier;
import java.security.SecureRandom;
import java.util.Base64;

public class SessionIdSupplierImpl implements SessionIdSupplier {

  private final SecureRandom secureRandom = new SecureRandom();
  private final Base64.Encoder encoder = Base64.getEncoder().withoutPadding();

  @Override
  public String get() {

    var bytes = new byte[32];
    secureRandom.nextBytes(bytes);

    return encoder.encodeToString(bytes);
  }
}
