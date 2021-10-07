package io.activated.pipeline.key;

import java.util.function.Supplier;
import org.reactivestreams.Publisher;

// TODO - Replace with Guava extends Source<Key>
public interface KeyStrategy extends Supplier<Publisher<Key>> {}
