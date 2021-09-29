package io.activated.pipeline.internal;

import io.activated.objectdiff.Renderable;
import io.activated.pipeline.key.Key;

public interface ChangeLogger {
  void initial(Key key, String stateName, Renderable initial);

  void change(Key key, String stateName, String actionName, Renderable action, Renderable diff);

  void moveKey(Key key);
}
