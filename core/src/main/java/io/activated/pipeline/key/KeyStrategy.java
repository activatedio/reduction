package io.activated.pipeline.key;

import io.activated.pipeline.Context;
import java.util.function.Function;
import reactor.core.publisher.Mono;

// TODO - Replace with Guava extends Source<Key>
public interface KeyStrategy extends Function<Context, Mono<Key>> {}
