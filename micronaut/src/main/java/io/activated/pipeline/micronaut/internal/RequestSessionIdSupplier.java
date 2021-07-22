package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.env.SessionIdSupplier;
import io.micronaut.runtime.http.scope.RequestScope;

import javax.inject.Named;

@RequestScope
@Named("request")
public class RequestSessionIdSupplier implements SessionIdSupplier {

  @Override
  public String get() {
    var value = SessionIdFilter.SESSION_ID_HOLDER.get();
    return value;
  }
}
