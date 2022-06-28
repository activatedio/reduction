package io.activated.pipeline.micronaut;

import io.activated.pipeline.Context;
import io.micronaut.http.HttpRequest;
import reactor.core.publisher.Mono;

public interface ContextBuilder {

  Mono<Context> build(HttpRequest<?> request, Context context);

  default int order() {
    return 0;
  }
}
