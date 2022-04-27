package io.activated.pipeline.micronaut.fixtures;

import io.activated.pipeline.Context;
import io.activated.pipeline.StateGuard;
import jakarta.inject.Singleton;

@Singleton
public class DummyStateGuard1 implements StateGuard<DummyState> {

  @Override
  public void guardGlobal() {}

  @Override
  public void guard(Context context, DummyState state) {}
}
