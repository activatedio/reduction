package io.activated.pipeline.key;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.activated.pipeline.Constants;
import io.activated.pipeline.Context;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class SessionKeyStrategyTest {

  private SessionKeyStrategy unit;

  private final String sessionId = "test-session-id";

  @BeforeEach
  public void setUp() {
    unit = new SessionKeyStrategy();
  }

  @Test
  public void get_noHeader() {

    var context = new Context();

    assertThatThrownBy(() -> Mono.from(unit.apply(context)).block())
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("pipeline-session-id not provided in header");
  }

  @Test
  public void get() {

    var context = new Context();
    context.getHeaders().put(Constants.SESSION_ID_CONTEXT_KEY, List.of(sessionId));

    var got =
        Mono.from(unit.apply(context))
            .contextWrite(ctx -> ctx.put(Constants.SESSION_ID_CONTEXT_KEY, sessionId))
            .block();

    var reference = new Key();

    reference.setValue(sessionId);
    assertThat(got).isEqualTo(reference);
  }
}
