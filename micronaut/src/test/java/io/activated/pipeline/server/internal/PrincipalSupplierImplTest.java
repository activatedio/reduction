package io.activated.pipeline.server.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class PrincipalSupplierImplTest {

  @Mock private HttpServletRequest request;

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

    when(request.getUserPrincipal()).thenReturn(principal);

    assertThat(unit.get()).isEqualTo(principal);

    verify(request).getUserPrincipal();
    verifyNoMoreInteractions(request);
  }

  @Test
  public void get_null() {

    var unit = new PrincipalSupplierImpl(request);

    when(request.getUserPrincipal()).thenReturn(null);

    assertThat(unit.get()).isNull();

    verify(request).getUserPrincipal();
    verifyNoMoreInteractions(request);
  }
}
