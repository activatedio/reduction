package io.activated.pipeline.key;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import io.activated.pipeline.Constants;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class SessionKeyStrategyTest {

  private SessionKeyStrategy unit;

  @BeforeEach
  public void setUp() {
    unit = new SessionKeyStrategy();
  }

  @Test
  public void get_notInContext() {
    assertThatThrownBy(() -> Mono.from(unit.get()).block())
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage("Context is empty");
  }

  @Test
  public void get() {

    var sessionId = "test-session-id";

    var got =
        Mono.from(unit.get())
            .contextWrite(ctx -> ctx.put(Constants.SESSION_ID_CONTEXT_KEY, sessionId))
            .block();

    var reference = new Key();

    reference.setValue(sessionId);
    assertThat(got).isEqualTo(reference);
  }
}
