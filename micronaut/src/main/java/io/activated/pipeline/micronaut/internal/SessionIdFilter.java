package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.PipelineConfig;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.cookie.SameSite;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Filter
public class SessionIdFilter implements HttpServerFilter {

  private final PipelineConfig config;

  public SessionIdFilter(PipelineConfig config) {
    this.config = config;
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
                                    UUID.randomUUID().toString(),
                                    (ctx, r) ->
                                        Mono.just(r)
                                            .map(
                                                _r ->
                                                    _r.cookie(
                                                        buildCookie(
                                                            Cookie.of(
                                                                config.getSessionIdKeyName(),
                                                                ctx.sessionId)))))))
                    .orElseThrow())
        .flatMap(
            ctx ->
                Mono.from(chain.proceed(ctx.request))
                    .flatMap(resp -> ctx.postProcessor.apply(ctx, resp)));
  }

  private Cookie buildCookie(Cookie cookie) {

    if (config.isDevelopmentMode()) {
      return cookie;
    } else {
      return cookie.secure(true).httpOnly(true).sameSite(SameSite.Strict);
    }
  }
}
