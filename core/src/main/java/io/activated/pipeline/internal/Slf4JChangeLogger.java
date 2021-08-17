package io.activated.pipeline.internal;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4JChangeLogger extends AbstractChangeLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger("Pipeline.Change");

  @Override
  public void logInternal(Map<String, Object> values) {
    LOGGER.info("Change: {}", values);
  }
}
