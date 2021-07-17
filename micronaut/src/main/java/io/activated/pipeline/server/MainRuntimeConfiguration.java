package io.activated.pipeline.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pipeline")
public class MainRuntimeConfiguration {

  private String redisHost = "localhost";
  private int redisPort = 6379;

  public String getRedisHost() {
    return redisHost;
  }

  public void setRedisHost(String redisHost) {
    this.redisHost = redisHost;
  }

  public int getRedisPort() {
    return redisPort;
  }

  public void setRedisPort(int redisPort) {
    this.redisPort = redisPort;
  }
}
