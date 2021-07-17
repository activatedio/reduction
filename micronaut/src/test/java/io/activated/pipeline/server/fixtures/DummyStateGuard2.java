package io.activated.pipeline.server.fixtures;

import io.activated.pipeline.StateGuard;

public class DummyStateGuard2 implements StateGuard<DummyState> {

  @Override
  public void guardGlobal() {}

  @Override
  public void guard(DummyState state) {}
}
