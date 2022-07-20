package io.activated.pipeline.micronaut;

import java.util.Optional;
import javax.validation.constraints.NotNull;

public class StubMicronautPipelineConfiguration implements MicronautPipelineConfiguration {

  @Override
  public int getStateExpireSeconds() {
    return 1234;
  }

  @Override
  public String getSessionIdKeyName() {
    return "test-session-id-key";
  }

  @Override
  public boolean isDevelopmentMode() {
    return false;
  }

  @Override
  public Optional<String> getCookieDomain() {
    return Optional.empty();
  }

  @Override
  public String getRedisHost() {
    return "test-redis-host";
  }

  @Override
  public int getRedisPort() {
    return 1234;
  }

  @Override
  public @NotNull String[] getScanPackages() {
    return new String[] {"test.package"};
  }
}
