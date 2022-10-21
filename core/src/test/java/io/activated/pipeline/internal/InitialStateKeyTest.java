package io.activated.pipeline.internal;

import static org.assertj.core.api.Assertions.assertThat;

import io.activated.pipeline.Context;
import io.activated.pipeline.InitialState;
import io.activated.pipeline.JUnit5ModelTestSupport;
import io.activated.pipeline.PipelineException;
import io.activated.pipeline.fixtures.Dummy1;
import io.activated.pipeline.fixtures.Dummy2;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Mono;

public class InitialStateKeyTest extends JUnit5ModelTestSupport<InitialStateKey> {

  private static Stream<Arguments> fromInitialStateClassArguments() {
    return Stream.of(
        Arguments.of(
            String.class,
            null,
            new PipelineException("Invalid InitialState class: " + String.class.toString())),
        Arguments.of(DummyInitialState1.class, InitialStateKey.create(Dummy1.class), null),
        Arguments.of(DummyInitialState2.class, InitialStateKey.create(Dummy1.class), null));
  }

  @Override
  protected InitialStateKey makeReference() {
    return InitialStateKey.create(Dummy1.class);
  }

  @Override
  protected InitialStateKey modifyReference(final InitialStateKey input) {
    return InitialStateKey.create(Dummy2.class);
  }

  @ParameterizedTest
  @MethodSource("fromInitialStateClassArguments")
  public void fromInitialStateClass(
      final Class<?> input,
      final InitialStateKey<?> expected,
      final RuntimeException expectedException) {

    try {
      final var got = InitialStateKey.fromInitialStateClass(input);
      assertThat(got).isEqualTo(expected);
    } catch (final RuntimeException e) {
      assertThat(expectedException).isNotNull();
      if (e != null) {
        assertThat(e).isInstanceOf(expectedException.getClass());
        assertThat(e.getMessage()).isEqualTo(expectedException.getMessage());
      }
    }
  }

  public static class DummyInitialState1 implements InitialState<Dummy1> {
    @Override
    public Mono<Dummy1> initial(Context context) {
      return Mono.empty();
    }

    @Override
    public Dummy1 zero() {
      return null;
    }
  }

  public static class DummyInitialState2
      implements InitialState<Dummy1>, Comparable<DummyInitialState2> {
    @Override
    public Mono<Dummy1> initial(Context context) {
      return Mono.empty();
    }

    @Override
    public Dummy1 zero() {
      return null;
    }

    @Override
    public int compareTo(final DummyInitialState2 o) {
      return 0;
    }
  }
}
