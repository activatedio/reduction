package io.activated.pipeline.key;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import io.activated.pipeline.PipelineException;
import io.activated.pipeline.env.PrincipalSupplier;
import io.activated.pipeline.env.SessionIdSupplier;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith({MockitoExtension.class})
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class SessionUserUpgradeKeyStrategyTest {

  private SessionUserUpgradeKeyStrategy unit;

  @Mock private SessionIdSupplier sessionIdSupplier;

  @Mock private PrincipalSupplier principalSupplier;

  @BeforeEach
  public void setUp() {

    unit = new SessionUserUpgradeKeyStrategy(sessionIdSupplier, principalSupplier);
  }

  @Test
  public void get_null() {
    doTestMissing(null);
  }

  @Test
  public void get_empty() {
    doTestMissing("");
  }

  @Test
  public void get_blank() {
    doTestMissing("  ");
  }

  private void doTestMissing(String input) {

    when(sessionIdSupplier.get()).thenReturn(input);

    try {
      unit.get();
      fail("Exception should have been thrown");
    } catch (PipelineException e) {
      assertThat(e.getMessage()).isEqualTo("Could not obtain key from session");
    }

    verify(sessionIdSupplier).get();

    verifyNoMoreInteractions(sessionIdSupplier);
  }

  @Test
  public void get_sessionIdNoUserDetails() {

    var sessionId = "test-session-id";

    when(sessionIdSupplier.get()).thenReturn(sessionId);
    when(principalSupplier.get()).thenReturn(null);

    var got = unit.get();

    var reference = new Key();

    reference.setValue(sessionId);

    assertThat(got).isEqualTo(reference);

    verify(sessionIdSupplier).get();
    verify(principalSupplier).get();

    verifyNoMoreInteractions(sessionIdSupplier, principalSupplier);
  }

  @Test
  public void get_sessionIdUserDetails() {

    var sessionId = "test-session-id";
    var userId = "test-user-id";

    var principal =
        new Principal() {

          @Override
          public String getName() {
            return userId;
          }
        };

    when(sessionIdSupplier.get()).thenReturn(sessionId);
    when(principalSupplier.get()).thenReturn(principal);

    var got = unit.get();

    var reference = new Key();

    reference.setMoveFrom(sessionId);
    reference.setValue(String.format("%s_%s", sessionId, userId));

    assertThat(got).isEqualTo(reference);

    verify(sessionIdSupplier).get();
    verify(principalSupplier).get();

    verifyNoMoreInteractions(sessionIdSupplier, principalSupplier);
  }
}
