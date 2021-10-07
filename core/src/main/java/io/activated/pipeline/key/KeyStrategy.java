package io.activated.pipeline.key;

import io.activated.pipeline.Context;
import java.util.function.Function;
import org.reactivestreams.Publisher;

// TODO - Replace with Guava extends Source<Key>
public interface KeyStrategy extends Function<Context, Publisher<Key>> {}
