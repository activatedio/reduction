package io.activated.pipeline.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

class DiffTest {

  @Test
  public void clear() throws IOException {
    StringWriter w = new StringWriter();
    Diff.CLEAR.render(w);

    assertThat(w.toString()).isEqualTo("<cleared>");
  }
}
