package io.activated.pipeline.micronaut.cart;

import io.activated.pipeline.StateGuard;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class NoOpStateGuard1 implements StateGuard<Cart> {

  private final Logger LOGGER = LoggerFactory.getLogger(NoOpStateGuard1.class);

  @Override
  public void guardGlobal() {

    LOGGER.info("Do nothing");
  }

  @Override
  public void guard(Cart state) {

    LOGGER.info("Do nothing with state: {}", state);
  }
}
