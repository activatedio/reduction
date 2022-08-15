package io.activated.pipeline.micronaut;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("pipeline")
public class MainRuntimeConfiguration {

  private String redisHost = "localhost";
  private int redisPort = 6379;
  private String[] scanPackages;

  private int sessionLockAcquireSeconds = 10;

  private int sessionLockLengthSeconds = 30;

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

  public int getSessionLockAcquireSeconds() {
    return sessionLockAcquireSeconds;
  }

  public void setSessionLockAcquireSeconds(int sessionLockAcquireSeconds) {
    this.sessionLockAcquireSeconds = sessionLockAcquireSeconds;
  }

  public int getSessionLockLengthSeconds() {
    return sessionLockLengthSeconds;
  }

  public void setSessionLockLengthSeconds(int sessionLockLengthSeconds) {
    this.sessionLockLengthSeconds = sessionLockLengthSeconds;
  }
}
