package io.activated.pipeline.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pipeline.changelogging")
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
