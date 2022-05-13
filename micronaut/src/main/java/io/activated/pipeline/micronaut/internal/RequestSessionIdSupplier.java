package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.env.SessionIdSupplier;
import io.micronaut.http.context.ServerRequestContext;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
@Named("request")
public class RequestSessionIdSupplier implements SessionIdSupplier {

  @Override
  public String get() {
    return ServerRequestContext.currentRequest()
        .orElseThrow()
        .getAttribute(Constants.SESSION_ID_ATTRIBUTE_KEY, String.class)
        .orElseThrow();
  }
}
