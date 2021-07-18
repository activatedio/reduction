package io.activated.pipeline.server.internal;

import io.activated.pipeline.env.SessionIdSupplier;

import javax.inject.Inject;

import io.micronaut.http.HttpRequest;
import io.micronaut.runtime.http.scope.RequestScope;

@RequestScope
public class MicronautSessionIdSource implements SessionIdSupplier {

  // Spring knows how to scope this bean properly
  private final HttpRequest<?> request;

  @Inject
  public MicronautSessionIdSource(final HttpRequest<?> request) {
    this.request = request;
  }

  @Override
  public String get() {
    return (String) request.getAttribute(Constants.SESSION_ID_ATTRIBUTE_NAME).get();
  }
}
