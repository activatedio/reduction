package io.activated.pipeline.internal;

import static org.assertj.core.api.Assertions.assertThat;

import io.activated.pipeline.fixtures.Dummy1;
import io.activated.pipeline.fixtures.DummyState;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SnapshotterImplTest {

  private SnapshotterImpl unit;
  private DummyState input;
  private DummyState input2;

  @BeforeEach
  public void setUp() {

    unit = new SnapshotterImpl();

    input = new DummyState();
    input.setValue("abcd");

    input2 = new DummyState();
    input2.setValue("abcd2");
  }

  @Test
  public void snapshot_empty() throws IOException {

    var got = unit.snapshot(new Dummy1());

    var w = new StringWriter();
    got.render(w);

    assertThat(w.toString()).isEqualTo("{}");
  }

  @Test
  public void snapshot_scenario() throws IOException {

    var got = unit.snapshot(input);

    var w = new StringWriter();
    got.render(w);

    assertThat(w.toString()).isEqualTo("{\"value\":\"abcd\"}");

    var got2 = unit.snapshot(input2);

    var diff = got2.diff(got);

    var w2 = new StringWriter();
    diff.render(w2);

    assertThat(w2.toString())
        .isEqualTo("[{\"op\":\"replace\",\"path\":\"/value\",\"value\":\"abcd2\"}]");
  }
}
