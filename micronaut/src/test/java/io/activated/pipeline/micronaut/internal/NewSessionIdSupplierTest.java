package io.activated.pipeline.micronaut.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class NewSessionIdSupplierTest {

  @Test
  public void get() {

    var unit = new NewSessionIdSupplier();

    var iterations = 10000;
    var results = new HashSet<String>();

    var pattern = Pattern.compile("[A-Za-z0-9+\\/]{43}");

    for (int i = 0; i < iterations; i++) {

      var result = unit.get();
      assertThat(result).matches(pattern);
      results.add(result);
    }

    assertThat(results).hasSize(iterations);
  }
}
