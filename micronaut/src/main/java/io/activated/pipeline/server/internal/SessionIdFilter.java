package io.activated.pipeline.server.internal;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.FilterChain;
import io.micronaut.http.filter.HttpFilter;
import org.reactivestreams.Publisher;

@Filter("/**")
public class SessionIdFilter implements HttpFilter {

  private static final String HEADER_NAME = "pipeline-session-id";

  @Override
  public Publisher<? extends HttpResponse<?>> doFilter(HttpRequest<?> request, FilterChain chain) {

    var sessionId = request.getHeaders().get(HEADER_NAME);

    if (sessionId != null) {
      request.setAttribute(Constants.SESSION_ID_ATTRIBUTE_NAME, sessionId);
    }

    return chain.proceed(request);
  }

}
