package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.env.SessionIdSupplier;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.security.SecureRandom;
import java.util.Base64;

@Singleton
@Named("new")
public class NewSessionIdSupplier implements SessionIdSupplier {

  private final SecureRandom secureRandom = new SecureRandom();
  private final Base64.Encoder encoder = Base64.getEncoder().withoutPadding();

  @Override
  public String get() {

    var bytes = new byte[32];
    secureRandom.nextBytes(bytes);

    return encoder.encodeToString(bytes);
  }
}
