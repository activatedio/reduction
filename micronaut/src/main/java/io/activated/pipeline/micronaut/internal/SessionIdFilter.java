package io.activated.pipeline.micronaut.internal;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.FilterChain;
import io.micronaut.http.filter.HttpFilter;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

@Filter("/**")
public class SessionIdFilter implements HttpFilter {

  static ThreadLocal<String> SESSION_ID_HOLDER = new ThreadLocal<>();

  @Override
  public Publisher<? extends HttpResponse<?>> doFilter(HttpRequest<?> request, FilterChain chain) {

    var sessionId = request.getHeaders().get(Constants.SESSION_ID_REQUEST_HEADER_NAME);

    if (sessionId != null) {
      SESSION_ID_HOLDER.set(sessionId);
    }

    return Flowable.fromPublisher(chain.proceed(request)).doFinally(() -> {
      SESSION_ID_HOLDER.set(null);
    });

  }
}
