package io.activated.pipeline.micronaut.fixtures;

import io.activated.pipeline.StateGuard;
import javax.inject.Singleton;

@Singleton
public class DummyStateGuard2 implements StateGuard<DummyState> {

  @Override
  public void guardGlobal() {}

  @Override
  public void guard(DummyState state) {}
}
