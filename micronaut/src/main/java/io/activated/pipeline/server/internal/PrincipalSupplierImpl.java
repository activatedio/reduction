package io.activated.pipeline.server.internal;

import io.activated.pipeline.env.PrincipalSupplier;
import io.micronaut.http.HttpRequest;
import io.micronaut.runtime.http.scope.RequestScope;

import javax.inject.Inject;
import java.security.Principal;
import java.util.Optional;

@RequestScope
public class PrincipalSupplierImpl implements PrincipalSupplier {

  private final HttpRequest<?> request;

  @Inject
  public PrincipalSupplierImpl(HttpRequest request) {
    this.request = request;
  }

  @Override
  public Optional<Principal> get() {
    return request.getUserPrincipal();
  }
}
