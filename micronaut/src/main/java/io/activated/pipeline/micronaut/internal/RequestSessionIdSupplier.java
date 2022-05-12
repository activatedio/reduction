package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.env.SessionIdSupplier;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
@Named("request")
public class RequestSessionIdSupplier implements SessionIdSupplier {

  @Override
  public String get() {
    throw new UnsupportedOperationException();
    /*
    return ServerRequestContext.currentRequest()
        .orElseThrow()
        .getAttribute(Constants.SESSION_ID_REQUEST_KEY, String.class).orElseThrow();

     */
  }
}
