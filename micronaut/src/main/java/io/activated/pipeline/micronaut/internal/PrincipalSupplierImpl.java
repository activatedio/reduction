package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.env.PrincipalSupplier;
import io.micronaut.http.HttpRequest;
import io.micronaut.runtime.http.scope.RequestScope;
import java.security.Principal;
import java.util.Optional;
import javax.inject.Inject;

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
