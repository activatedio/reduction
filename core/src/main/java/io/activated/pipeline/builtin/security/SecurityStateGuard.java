package io.activated.pipeline.builtin.security;

import io.activated.pipeline.StateGuard;
import io.activated.pipeline.env.PrincipalSupplier;

/**
 * Requires a logged in user to continue
 *
 * @param <S>
 */
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
  public void guard(Object state) {
    guardGlobal();
  }
}
