package io.activated.pipeline;

public class PipelineConfig {

  private int stateExpireInSeconds = 7200;
  private int authenticationRefreshFailResetSeconds = 120;
  private int refreshTokenMarginSeconds = 240;

  private int sessionLockAcquireSeconds = 10;

  private int sessionLockLengthSeconds = 30;

  public int getStateExpireInSeconds() {
    return stateExpireInSeconds;
  }

  public void setStateExpireInSeconds(int stateExpireInSeconds) {
    this.stateExpireInSeconds = stateExpireInSeconds;
  }

  public int getAuthenticationRefreshFailResetSeconds() {
    return authenticationRefreshFailResetSeconds;
  }

  public void setAuthenticationRefreshFailResetSeconds(int authenticationRefreshFailResetSeconds) {
    this.authenticationRefreshFailResetSeconds = authenticationRefreshFailResetSeconds;
  }

  public int getRefreshTokenMarginSeconds() {
    return refreshTokenMarginSeconds;
  }

  public void setRefreshTokenMarginSeconds(int refreshTokenMarginSeconds) {
    this.refreshTokenMarginSeconds = refreshTokenMarginSeconds;
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
