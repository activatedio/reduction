package io.activated.pipeline.micronaut;

import io.activated.pipeline.PipelineConfig;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.bind.annotation.Bindable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ConfigurationProperties("pipeline")
public interface MicronautPipelineConfiguration extends PipelineConfig {

  @Override
  @Bindable(defaultValue = "28800")
  int getStateExpireSeconds();

  @Override
  @Bindable(defaultValue = "pipeline-session-id")
  String getSessionIdKeyName();

  @Override
  @Bindable(defaultValue = "false")
  boolean isDevelopmentMode();

  @NotBlank
  @Bindable(defaultValue = "localhost")
  String getRedisHost();

  @Bindable(defaultValue = "6379")
  int getRedisPort();

  @NotNull
  String[] getScanPackages();
}
