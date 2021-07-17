package io.activated.pipeline.key;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import io.activated.pipeline.PipelineException;
import io.activated.pipeline.env.SessionIdSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith({MockitoExtension.class})
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class SessionKeyStrategyTest {

  private SessionKeyStrategy unit;

  @Mock private SessionIdSupplier sessionIdSupplier;

  @BeforeEach
  public void setUp() {

    unit = new SessionKeyStrategy(sessionIdSupplier);
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
  public void get() {

    var sessionId = "test-session-id";

    when(sessionIdSupplier.get()).thenReturn(sessionId);

    var got = unit.get();

    var reference = new Key();

    reference.setValue(sessionId);

    assertThat(got).isEqualTo(reference);

    verify(sessionIdSupplier).get();

    verifyNoMoreInteractions(sessionIdSupplier);
  }
}
