package io.activated.pipeline.micronaut.e2e;

import io.activated.pipeline.StateGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class NoOpStateGuard2 implements StateGuard<Cart> {

  private final Logger LOGGER = LoggerFactory.getLogger(NoOpStateGuard2.class);

  @Override
  public void guardGlobal() {

    LOGGER.info("Do nothing");
  }

  @Override
  public void guard(Cart state) {

    LOGGER.info("Do nothing with state: {}", state);
  }
}
