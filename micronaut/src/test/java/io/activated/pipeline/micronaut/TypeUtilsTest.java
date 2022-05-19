package io.activated.pipeline.micronaut;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.activated.pipeline.micronaut.fixtures.DummyExternalState;
import io.activated.pipeline.micronaut.fixtures.DummyInternalState;
import io.activated.pipeline.micronaut.fixtures.DummyState;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TypeUtilsTest {

  @ParameterizedTest
  @MethodSource("isExportableData")
  public void isExportable(Class<?> input, boolean expected) {
    assertThat(TypeUtils.isExportable(input)).isEqualTo(expected);
  }

  public static Stream<Arguments> isExportableData() {
    return Stream.of(
        Arguments.of(DummyState.class, false), Arguments.of(DummyInternalState.class, true));
  }

  @Test
  public void toExportable() {

    assertThat(TypeUtils.toExportable(DummyInternalState.class))
        .isEqualTo(DummyExternalState.class);
  }

  @Test
  public void toExportable_exception() {

    assertThatThrownBy(() -> TypeUtils.toExportable(DummyState.class))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("input class is not exportable");
  }
}
