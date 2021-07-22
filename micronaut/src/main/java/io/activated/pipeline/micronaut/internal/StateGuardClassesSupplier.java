package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.StateGuard;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface StateGuardClassesSupplier
    extends Supplier<Map<Class<?>, List<Class<? extends StateGuard<?>>>>> {}
