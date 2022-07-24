package io.activated.pipeline.micronaut;

import io.activated.pipeline.Context;
import io.micronaut.http.HttpRequest;
import reactor.core.publisher.Mono;

public interface ContextFactory {

  Mono<Context> create(HttpRequest<?> request);
}
