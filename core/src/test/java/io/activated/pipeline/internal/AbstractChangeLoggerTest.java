package io.activated.pipeline.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import io.activated.pipeline.key.Key;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractChangeLoggerTest {

  private static class TestableAbstractChangeLogger extends AbstractChangeLogger {

    Map<String, Object> lastValues;

    @Override
    public void logInternal(Map<String, Object> values) {
      this.lastValues = values;
    }
  }

  private TestableAbstractChangeLogger unit;

  private final Key key;
  private final Key keyWithMoveFrom;
  private final String stateName = "test-state-name";
  private final String actionName = "test-action-name";

  private final String initialValue = "initial";
  private final String actionValue = "action";
  private final String diffValue = "diff";

  private final Renderable initial = makeRenderable(initialValue);
  private final Renderable action = makeRenderable(actionValue);
  private final Renderable diff = makeRenderable(diffValue);

  public AbstractChangeLoggerTest() {

    key = new Key();
    key.setValue("test-key");

    keyWithMoveFrom = new Key();
    keyWithMoveFrom.setValue("test-key-move-from");
    keyWithMoveFrom.setMoveFrom("test-key-move-from-move-from-value");
  }

  private Renderable makeRenderable(String initial) {

    return new Renderable() {
      @Override
      public void render(Writer w) throws IOException {
        w.write(initial);
      }
    };
  }

  @BeforeEach
  public void setUp() {

    unit = new TestableAbstractChangeLogger();
  }

  @Test
  public void initial() {

    unit.initial(key, stateName, initial);

    assertThat(unit.lastValues)
        .containsOnly(
            entry("key", "test-key"),
            entry("stateName", stateName),
            entry("initial", initialValue));
  }

  @Test
  public void change() {

    unit.change(key, stateName, actionName, action, diff);

    assertThat(unit.lastValues)
        .containsOnly(
            entry("key", "test-key"),
            entry("stateName", stateName),
            entry("actionName", actionName),
            entry("action", actionValue),
            entry("diff", diffValue));
  }

  @Test
  public void keyMove() {

    unit.moveKey(keyWithMoveFrom);

    assertThat(unit.lastValues)
        .containsOnly(
            entry("key", "test-key-move-from"),
            entry("keyMovedFrom", "test-key-move-from-move-from-value"));
  }
}
