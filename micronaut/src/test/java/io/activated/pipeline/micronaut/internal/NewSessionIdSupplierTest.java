package io.activated.pipeline.micronaut.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class NewSessionIdSupplierTest {

  @Test
  public void get() {

    var unit = new NewSessionIdSupplier();

    var iterations = 500;
    var results = new HashSet<String>();

    for (int i = 0; i < iterations; i++) {

      var result = unit.get();
      // Just be sure we can parse it
      var got = UUID.fromString(result);
      assertThat(got.version()).isEqualTo(4);
      results.add(result);
    }

    assertThat(results).hasSize(iterations);
  }
}
