package io.activated.pipeline.key;

import java.util.function.Function;
import java.util.function.Supplier;

import io.activated.pipeline.Context;
import org.reactivestreams.Publisher;

// TODO - Replace with Guava extends Source<Key>
public interface KeyStrategy extends Function<Context, Publisher<Key>> {}
