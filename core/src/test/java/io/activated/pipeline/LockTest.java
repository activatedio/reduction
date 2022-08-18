package io.activated.pipeline;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class LockTest {

  @Test
  public void nesting() {

    var unit = new Lock("test-key");

    assertThat(unit.getNesting()).isEqualTo(0);
    unit.incrementNesting();

    assertThat(unit.getNesting()).isEqualTo(1);

    unit.incrementNesting();

    assertThat(unit.getNesting()).isEqualTo(2);

    unit.decrementNesting();
    unit.decrementNesting();

    assertThat(unit.getNesting()).isEqualTo(0);
  }
}
