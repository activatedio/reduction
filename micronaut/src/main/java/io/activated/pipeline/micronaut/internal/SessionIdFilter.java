package io.activated.pipeline.micronaut.internal;

import com.google.common.annotations.VisibleForTesting;
import io.activated.pipeline.Constants;
import io.activated.pipeline.PipelineConfig;
import io.activated.pipeline.env.SessionIdSupplier;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.cookie.SameSite;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import jakarta.inject.Named;
import java.util.Optional;
import java.util.function.BiFunction;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Filter(Filter.MATCH_ALL_PATTERN)
public class SessionIdFilter implements HttpServerFilter {

  private final PipelineConfig config;

  private final SessionIdSupplier sessionIdSupplier;

  public SessionIdFilter(PipelineConfig config, @Named("new") SessionIdSupplier sessionIdSupplier) {
    this.config = config;
    this.sessionIdSupplier = sessionIdSupplier;
  }

  private static class SessionCookieContext {
    private final HttpRequest<?> request;
    private final String sessionId;
    private final BiFunction<
            SessionCookieContext, MutableHttpResponse<?>, Mono<MutableHttpResponse<?>>>
        postProcessor;

    private SessionCookieContext(
        HttpRequest<?> request,
        String sessionId,
        BiFunction<SessionCookieContext, MutableHttpResponse<?>, Mono<MutableHttpResponse<?>>>
            postProcessor) {
      this.request = request;
      this.sessionId = sessionId;
      this.postProcessor = postProcessor;
    }
  }

  @Override
  public Publisher<MutableHttpResponse<?>> doFilter(
      HttpRequest<?> request, ServerFilterChain chain) {

    return Mono.just(request)
        .map(
            req ->
                req.getCookies()
                    .findCookie(config.getSessionIdKeyName())
                    .map(c -> new SessionCookieContext(req, c.getValue(), (ctx, r) -> Mono.just(r)))
                    .or(
                        () ->
                            Optional.of(
                                new SessionCookieContext(
                                    req,
                                    sessionIdSupplier.get(),
                                    (ctx, r) ->
                                        Mono.just(r)
                                            .map(
                                                _r ->
                                                    _r.cookie(
                                                        buildCookie(
                                                            createCookie(
                                                                config.getSessionIdKeyName(),
                                                                ctx.sessionId)))))))
                    .orElseThrow())
        .flatMap(
            ctx ->
                Mono.from(
                        chain.proceed(
                            ctx.request.setAttribute(
                                Constants.SESSION_ID_ATTRIBUTE_KEY, ctx.sessionId)))
                    .flatMap(resp -> ctx.postProcessor.apply(ctx, resp)));
  }

  @VisibleForTesting
  protected Cookie createCookie(String name, String value) {
    var c = Cookie.of(name, value);

    // TODO - Update unit tests to cover cookie domain being set
    if (config.getCookieDomain().isPresent()) {
      var domain = config.getCookieDomain().get();
      c.domain(domain);
    }

    return c;
  }

  private Cookie buildCookie(Cookie cookie) {

    if (config.isDevelopmentMode()) {
      return cookie;
    } else {
      return cookie.secure(true).httpOnly(true).sameSite(SameSite.Strict);
    }
  }
}
