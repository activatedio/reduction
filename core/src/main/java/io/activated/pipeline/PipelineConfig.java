package io.activated.pipeline;

public interface PipelineConfig {

  int getStateExpireSeconds();

  String getSessionIdKeyName();

  boolean isDevelopmentMode();
}
