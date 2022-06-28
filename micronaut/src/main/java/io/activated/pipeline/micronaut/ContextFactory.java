package io.activated.pipeline.micronaut;

import io.activated.pipeline.Context;
import reactor.core.publisher.Mono;

public interface ContextFactory {

  Mono<Context> create();
}
