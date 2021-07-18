package io.activated.pipeline.server;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("pipeline")
public class MainRuntimeConfiguration {

  private String redisHost = "localhost";
  private int redisPort = 6379;
  private String[] scanPackages;

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

  public String[] getScanPackages() {
    return scanPackages;
  }

  public void setScanPackages(String[] scanPackages) {
    this.scanPackages = scanPackages;
  }

}
