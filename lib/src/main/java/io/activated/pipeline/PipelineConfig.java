package io.activated.pipeline;

public class PipelineConfig {

  private int stateExpireInSeconds = 28800;
  private int authenticationRefreshFailResetSeconds = 120;
  private int refreshTokenMarginSeconds = 240;

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
}
