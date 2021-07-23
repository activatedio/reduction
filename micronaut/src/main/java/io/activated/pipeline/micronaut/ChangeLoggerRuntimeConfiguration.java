package io.activated.pipeline.micronaut;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("pipeline.changelogger")
public class ChangeLoggerRuntimeConfiguration {

  private String type;
  private String pulsarServiceUrl;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getPulsarServiceUrl() {
    return pulsarServiceUrl;
  }

  public void setPulsarServiceUrl(String pulsarServiceUrl) {
    this.pulsarServiceUrl = pulsarServiceUrl;
  }
}
