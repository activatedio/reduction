package io.activated.pipeline.internal;

import static org.assertj.core.api.Assertions.assertThat;

import io.activated.base.JUnit5ModelTestSupport;
import io.activated.pipeline.PipelineException;
import io.activated.pipeline.Reducer;
import io.activated.pipeline.fixtures.Dummy1;
import io.activated.pipeline.fixtures.Dummy2;
import io.activated.pipeline.fixtures.Dummy3;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ReducerKeyTest extends JUnit5ModelTestSupport<ReducerKey> {

  private static Stream<Arguments> fromReducerClassArguments() {
    return Stream.of(
        Arguments.of(
            String.class,
            null,
            new PipelineException("Invalid reducer class: " + String.class.toString())),
        Arguments.of(DummyReducer1.class, ReducerKey.create(Dummy1.class, Dummy2.class), null),
        Arguments.of(DummyReducer2.class, ReducerKey.create(Dummy1.class, Dummy2.class), null));
  }

  @Override
  protected ReducerKey makeReference() {
    return ReducerKey.create(Dummy1.class, Dummy2.class);
  }

  @Override
  protected ReducerKey modifyReference(final ReducerKey input) {
    return ReducerKey.create(Dummy1.class, Dummy3.class);
  }

  @ParameterizedTest
  @MethodSource("fromReducerClassArguments")
  public void fromReducerClass(
      final Class<?> input,
      final ReducerKey<?, ?> expected,
      final RuntimeException expectedException) {

    try {
      final var got = ReducerKey.fromReducerClass(input);
      assertThat(got).isEqualTo(expected);
    } catch (final RuntimeException e) {
      assertThat(expectedException).isNotNull();
      if (e != null) {
        assertThat(e).isInstanceOf(expectedException.getClass());
        assertThat(e.getMessage()).isEqualTo(expectedException.getMessage());
      }
    }
  }

  public static class DummyReducer1 implements Reducer<Dummy1, Dummy2> {
    @Override
    public void reduce(final Dummy1 state, final Dummy2 action) {}
  }

  public static class DummyReducer2 implements Reducer<Dummy1, Dummy2>, Comparable<DummyReducer2> {
    @Override
    public void reduce(final Dummy1 state, final Dummy2 action) {}

    @Override
    public int compareTo(final DummyReducer2 o) {
      return 0;
    }
  }
}
