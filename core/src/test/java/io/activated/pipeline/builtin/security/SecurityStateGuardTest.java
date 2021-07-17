package io.activated.pipeline.builtin.security;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import io.activated.pipeline.env.PrincipalSupplier;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class SecurityStateGuardTest {

  @Mock PrincipalSupplier principalSupplier;

  private final Principal principal =
      new Principal() {

        @Override
        public String getName() {
          return null;
        }
      };

  @Test
  public void guard_global_principalSet() {

    var unit = new SecurityStateGuard(principalSupplier);

    when(principalSupplier.get()).thenReturn(principal);

    unit.guardGlobal();

    verify(principalSupplier).get();
    verifyNoMoreInteractions(principalSupplier);
  }

  @Test
  public void guard_global_noPrincipal() {

    var unit = new SecurityStateGuard(principalSupplier);

    when(principalSupplier.get()).thenReturn(null);

    assertThatThrownBy(() -> unit.guardGlobal())
        .isInstanceOf(SecurityException.class)
        .hasMessage("Unauthenticated");

    verify(principalSupplier).get();
    verifyNoMoreInteractions(principalSupplier);
  }

  @Test
  public void guard_specific_principalSet() {

    var unit = new SecurityStateGuard(principalSupplier);

    when(principalSupplier.get()).thenReturn(principal);

    unit.guard(new Object());

    verify(principalSupplier).get();
    verifyNoMoreInteractions(principalSupplier);
  }

  @Test
  public void guard_specific_noPrincipal() {

    var unit = new SecurityStateGuard(principalSupplier);

    when(principalSupplier.get()).thenReturn(null);

    assertThatThrownBy(() -> unit.guard(new Object()))
        .isInstanceOf(SecurityException.class)
        .hasMessage("Unauthenticated");

    verify(principalSupplier).get();
    verifyNoMoreInteractions(principalSupplier);
  }
}
