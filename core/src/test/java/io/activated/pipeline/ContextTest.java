package io.activated.pipeline;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class ContextTest {

  @Test
  public void getHeaders() {
    // Check that we are case insensitive

    var ctx = new Context();

    var keyA = "aAa";
    var keyB = "bBb";
    var keyZ = "zzz";
    var valueA = List.of("valueAAA");
    var valueB = List.of("valueBBB");

    ctx.getHeaders().putAll(Map.of("aaa", valueA, "bbb", valueB));

    assertThat(ctx.getHeaders().get(keyZ)).isNull();
    assertThat(ctx.getHeaders().get(keyA)).isSameAs(valueA);
    assertThat(ctx.getHeaders().get(keyA.toLowerCase())).isSameAs(valueA);
    assertThat(ctx.getHeaders().get(keyA.toUpperCase())).isSameAs(valueA);
    assertThat(ctx.getHeaders().get(keyB)).isSameAs(valueB);
    assertThat(ctx.getHeaders().get(keyB.toLowerCase())).isSameAs(valueB);
    assertThat(ctx.getHeaders().get(keyB.toUpperCase())).isSameAs(valueB);
  }
}
