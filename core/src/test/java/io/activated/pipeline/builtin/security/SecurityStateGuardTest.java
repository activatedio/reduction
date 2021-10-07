package io.activated.pipeline.builtin.security;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import io.activated.pipeline.Context;
import io.activated.pipeline.env.PrincipalSupplier;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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

  private Context context;

  private final Principal principal =
      new Principal() {

        @Override
        public String getName() {
          return null;
        }
      };

  @BeforeEach
  public void setUp() {
    var context = new Context();
    context.getHeaders().put("header1", List.of("value1"));
  }

  @Test
  public void guard_global_principalSet() {

    var unit = new SecurityStateGuard(principalSupplier);

    when(principalSupplier.get()).thenReturn(Optional.of(principal));

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

    when(principalSupplier.get()).thenReturn(Optional.of(principal));

    unit.guard(context, new Object());

    verify(principalSupplier).get();
    verifyNoMoreInteractions(principalSupplier);
  }

  @Test
  public void guard_specific_noPrincipal() {

    var unit = new SecurityStateGuard(principalSupplier);

    when(principalSupplier.get()).thenReturn(null);

    assertThatThrownBy(() -> unit.guard(context, new Object()))
        .isInstanceOf(SecurityException.class)
        .hasMessage("Unauthenticated");

    verify(principalSupplier).get();
    verifyNoMoreInteractions(principalSupplier);
  }
}
