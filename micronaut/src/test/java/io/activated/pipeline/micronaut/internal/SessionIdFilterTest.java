package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.PipelineConfig;
import io.activated.pipeline.micronaut.StubMicronautPipelineConfiguration;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.filter.ServerFilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class SessionIdFilterTest {

  private final PipelineConfig config = new StubMicronautPipelineConfiguration();

  private final PipelineConfig configDevMode =
      new StubMicronautPipelineConfiguration() {
        @Override
        public boolean isDevelopmentMode() {
          return true;
        }
      };

  private final String sessionId = "test-session-id";

  @Mock private HttpRequest<?> request;

  @Mock private ServerFilterChain chain;

  private SessionIdFilter makeUnit(PipelineConfig config) {
    return new SessionIdFilter(config);
  }

  private void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(request);
  }

  @Test
  public void doFilter_cookieExists() {

    // makeUnit().doFilter(request)

  }
}
