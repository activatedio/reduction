package io.activated.pipeline.internal;

import com.google.common.collect.Maps;
import io.activated.pipeline.PipelineException;
import io.activated.pipeline.key.Key;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public abstract class AbstractChangeLogger implements ChangeLogger {

  @Override
  public void initial(Key key, String stateName, Renderable initial) {
    logInternal(makeValues(key.getValue(), null, stateName, null, initial, null, null));
  }

  @Override
  public void change(
      Key key, String stateName, String actionName, Renderable action, Renderable diff) {
    logInternal(makeValues(key.getValue(), null, stateName, actionName, null, action, diff));
  }

  @Override
  public void moveKey(Key key) {
    logInternal(makeValues(key.getValue(), key.getMoveFrom(), null, null, null, null, null));
  }

  private Map<String, Object> makeValues(
      String key,
      String keyMoveFrom,
      String stateName,
      String actionName,
      Renderable initial,
      Renderable action,
      Renderable diff) {

    Map<String, Object> values = Maps.newHashMap();

    values.put("key", key);

    if (keyMoveFrom != null) {
      values.put("keyMovedFrom", keyMoveFrom);
    }

    if (stateName != null) {
      values.put("stateName", stateName);
    }

    if (actionName != null) {
      values.put("actionName", actionName);
    }

    if (initial != null) {
      values.put("initial", renderToString(initial));
    }
    if (action != null) {
      values.put("action", renderToString(action));
    }
    if (diff != null) {
      values.put("diff", renderToString(diff));
    }

    return values;
  }

  private String renderToString(Renderable value) {

    StringWriter writer = new StringWriter();

    try {

      value.render(writer);
      return writer.toString();

    } catch (IOException e) {
      throw new PipelineException(e);
    }
  }

  public abstract void logInternal(Map<String, Object> values);
}
