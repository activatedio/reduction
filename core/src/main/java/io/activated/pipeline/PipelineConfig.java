package io.activated.pipeline;

import java.util.Optional;

public interface PipelineConfig {

  int getStateExpireSeconds();

  String getSessionIdKeyName();

  boolean isDevelopmentMode();

  Optional<String> getCookieDomain();
}
