package io.activated.pipeline.micronaut.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import io.micronaut.http.HttpRequest;
import java.security.Principal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class PrincipalSupplierImplTest {

  @Mock private HttpRequest<?> request;

  private final Principal principal =
      new Principal() {

        @Override
        public String getName() {
          return null;
        }
      };

  @Test
  public void get_valid() {

    var unit = new PrincipalSupplierImpl(request);

    when(request.getUserPrincipal()).thenReturn(Optional.of(principal));

    assertThat(unit.get()).isEqualTo(Optional.of(principal));

    verify(request).getUserPrincipal();
    verifyNoMoreInteractions(request);
  }

  @Test
  public void get_not_exists() {

    var unit = new PrincipalSupplierImpl(request);

    when(request.getUserPrincipal()).thenReturn(Optional.empty());

    assertThat(unit.get()).isEqualTo(Optional.empty());

    verify(request).getUserPrincipal();
    verifyNoMoreInteractions(request);
  }
}
