package io.activated.pipeline.key;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.activated.pipeline.Constants;
import io.activated.pipeline.Context;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Mono;

public class SessionKeyStrategyTest {

  private SessionKeyStrategy unit;

  private final String sessionIdValid = UUID.randomUUID().toString();

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
    context.getAttributes().put(Constants.SESSION_ID_ATTRIBUTE_KEY, sessionIdValid);

    var got = Mono.from(unit.apply(context)).block();

    var reference = new Key();

    reference.setValue(sessionIdValid);
    assertThat(got).isEqualTo(reference);
  }

  @ParameterizedTest
  @MethodSource("get_invalidData")
  public void get_invalid(String input) {

    var context = new Context();
    context.getAttributes().put(Constants.SESSION_ID_ATTRIBUTE_KEY, input);

    assertThatThrownBy(() -> Mono.from(unit.apply(context)).block())
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("invalid pipeline-session-id");
  }

  public static Stream<Arguments> get_invalidData() {

    return Stream.of(
        Arguments.of(""), Arguments.of("test-id")
        // Arguments.of("00000000-0000-0000-0000-000000000000"),
        // Arguments.of("00000000-0000-0000-0000-000000000001")
        );
  }
}
