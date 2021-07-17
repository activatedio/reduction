package io.activated.pipeline.key;

import io.activated.base.JUnit5ModelTestSupport;

public class KeyTest extends JUnit5ModelTestSupport<Key> {

  @Override
  protected Key makeReference() {
    var key = new Key();
    key.setValue("1");
    return key;
  }

  @Override
  protected Key modifyReference(Key key) {
    key.setValue("2");
    return key;
  }
}
