package io.activated.pipeline.key;

import org.reactivestreams.Publisher;

import java.util.function.Supplier;

// TODO - Replace with Guava extends Source<Key>
public interface KeyStrategy extends Supplier<Publisher<Key>> {}
