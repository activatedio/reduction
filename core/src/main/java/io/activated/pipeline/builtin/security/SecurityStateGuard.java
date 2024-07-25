package io.activated.pipeline.builtin.security;

import io.activated.pipeline.Context;
import io.activated.pipeline.StateGuard;
import io.activated.pipeline.env.PrincipalSupplier;

/** Requires a logged in user to continue */
public class SecurityStateGuard implements StateGuard<Object> {

  private final PrincipalSupplier principalSupplier;

  public SecurityStateGuard(PrincipalSupplier principalSupplier) {
    this.principalSupplier = principalSupplier;
  }

  @Override
  public void guardGlobal() {
    if (principalSupplier.get() == null) {
      throw new SecurityException("Unauthenticated");
    }
  }

  @Override
  public void guard(Context context, Object state) {
    guardGlobal();
  }
}
