package io.activated.pipeline.server.internal;

import io.activated.pipeline.Reducer;
import io.activated.pipeline.internal.ReducerKey;
import java.util.Map;
import java.util.function.Supplier;

public interface ReducerClassesSupplier
    extends Supplier<Map<ReducerKey<?, ?>, Class<? extends Reducer<?, ?>>>> {}
