package io.activated.pipeline.server.internal;

import io.activated.pipeline.InitialState;
import io.activated.pipeline.internal.InitialStateKey;
import java.util.Map;
import java.util.function.Supplier;

public interface InitialStateClassesSupplier
    extends Supplier<Map<InitialStateKey<?>, Class<? extends InitialState<?>>>> {}
