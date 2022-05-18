package io.activated.pipeline.micronaut.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.activated.pipeline.Constants;
import io.activated.pipeline.PipelineConfig;
import io.activated.pipeline.env.SessionIdSupplier;
import io.activated.pipeline.micronaut.StubMicronautPipelineConfiguration;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.cookie.Cookies;
import io.micronaut.http.cookie.SameSite;
import io.micronaut.http.filter.ServerFilterChain;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;

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

  @Mock private SessionIdSupplier sessionIdSupplier;

  @Mock private HttpRequest<?> request;

  @Mock private MutableHttpResponse<Object> response;

  @Mock private ServerFilterChain chain;

  @Mock private Cookies cookies;

  @Mock private Cookie cookie;

  private SessionIdFilter makeUnit(PipelineConfig config) {
    return new SessionIdFilter(config, sessionIdSupplier) {
      @Override
      protected Cookie createCookie(String name, String value) {
        assertThat(name).isEqualTo(config.getSessionIdKeyName());
        assertThat(value).isEqualTo(sessionId);
        return cookie;
      }
    };
  }

  private void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(request, chain, cookies, cookie, sessionIdSupplier);
  }

  @Test
  public void doFilter_cookieExists() {

    var unit = makeUnit(config);

    when(request.getCookies()).thenReturn(cookies);
    when(cookies.findCookie(config.getSessionIdKeyName())).thenReturn(Optional.of(cookie));
    when(cookie.getValue()).thenReturn(sessionId);
    when(request.setAttribute(Constants.SESSION_ID_ATTRIBUTE_KEY, sessionId))
        .thenReturn((HttpRequest) request);
    when(chain.proceed(request)).thenReturn(Mono.just(response));

    assertThat(Mono.from(unit.doFilter(request, chain)).block()).isSameAs(response);

    verifyNoMoreInteractions();
  }

  @Test
  public void doFilter_cookieNotExists_notDevMode() {

    var unit = makeUnit(config);

    when(request.getCookies()).thenReturn(cookies);
    when(cookies.findCookie(config.getSessionIdKeyName())).thenReturn(Optional.empty());
    when(sessionIdSupplier.get()).thenReturn(sessionId);
    when(cookie.secure(true)).thenReturn(cookie);
    when(cookie.httpOnly(true)).thenReturn(cookie);
    when(cookie.sameSite(SameSite.Strict)).thenReturn(cookie);
    when(response.cookie(cookie)).thenReturn(response);
    when(request.setAttribute(Constants.SESSION_ID_ATTRIBUTE_KEY, sessionId))
        .thenReturn((HttpRequest) request);
    when(chain.proceed(request)).thenReturn(Mono.just(response));

    assertThat(Mono.from(unit.doFilter(request, chain)).block()).isSameAs(response);

    verifyNoMoreInteractions();
  }

  @Test
  public void doFilter_cookieNotExists_devMode() {

    var unit = makeUnit(configDevMode);

    when(request.getCookies()).thenReturn(cookies);
    when(cookies.findCookie(config.getSessionIdKeyName())).thenReturn(Optional.empty());
    when(sessionIdSupplier.get()).thenReturn(sessionId);
    when(response.cookie(cookie)).thenReturn(response);
    when(request.setAttribute(Constants.SESSION_ID_ATTRIBUTE_KEY, sessionId))
        .thenReturn((HttpRequest) request);
    when(chain.proceed(request)).thenReturn(Mono.just(response));

    assertThat(Mono.from(unit.doFilter(request, chain)).block()).isSameAs(response);

    verifyNoMoreInteractions();
  }
}
