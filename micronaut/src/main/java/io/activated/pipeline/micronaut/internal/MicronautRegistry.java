package io.activated.pipeline.micronaut.internal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.activated.pipeline.InitialState;
import io.activated.pipeline.PipelineException;
import io.activated.pipeline.Reducer;
import io.activated.pipeline.StateGuard;
import io.activated.pipeline.annotations.Initial;
import io.activated.pipeline.annotations.Operation;
import io.activated.pipeline.annotations.State;
import io.activated.pipeline.internal.InitialStateKey;
import io.activated.pipeline.internal.ReducerKey;
import io.activated.pipeline.internal.Registry;
import io.activated.pipeline.key.KeyStrategy;
import io.activated.pipeline.micronaut.MainRuntimeConfiguration;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.io.scan.ClassPathAnnotationScanner;
import io.micronaut.core.util.ArgumentUtils;
import java.util.*;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MicronautRegistry implements Registry {

  private final ApplicationContext applicationContext;

  private final Set<Class<?>> stateTypes;
  private final Map<ReducerKey<?, ?>, Class<? extends Reducer>> reducers;
  private final Map<InitialStateKey<?>, Class<? extends InitialState>> initialStates;
  private final Map<Class<?>, List<Class<? extends StateGuard>>> stateGuardClasses;

  private final Map<Class<?>, Class<? extends KeyStrategy>> keyStrategies;

  @Inject
  public MicronautRegistry(
      final ApplicationContext applicationContext, final MainRuntimeConfiguration configuration) {

    this.applicationContext = applicationContext;
    var scanner = new ClassPathAnnotationScanner();

    ArgumentUtils.requireNonNull("configuration", configuration);
    ArgumentUtils.requireNonNull("configuration.scanPackages", configuration.getScanPackages());

    Set<Class<?>> stateTypes = Sets.newHashSet();
    Map<Class<?>, List<Class<? extends StateGuard>>> stateGuardClasses = Maps.newHashMap();
    Map<Class<?>, Class<? extends KeyStrategy>> keyStrategies = Maps.newHashMap();

    scanner
        .scan(State.class, configuration.getScanPackages())
        .forEach(
            (c) -> {
              stateTypes.add(c);
              registerStateGuards(stateGuardClasses, c);
              registerKeyStrategies(keyStrategies, c);
            });

    this.stateTypes = Collections.unmodifiableSet(stateTypes);
    this.stateGuardClasses = Collections.unmodifiableMap(stateGuardClasses);

    final Set<Class<? extends StateGuard>> guards = Sets.newHashSet();
    this.stateGuardClasses.values().forEach(guards::addAll);

    this.keyStrategies = keyStrategies;

    final Map<ReducerKey<?, ?>, Class<? extends Reducer>> reducers = Maps.newHashMap();
    scanner
        .scan(Operation.class, configuration.getScanPackages())
        .forEach(
            (c) -> {
              var key = ReducerKey.fromReducerClass(c);
              reducers.put(key, c);
            });
    this.reducers = Collections.unmodifiableMap(reducers);

    final Map<InitialStateKey<?>, Class<? extends InitialState<?>>> initialStates =
        Maps.newHashMap();
    scanner
        .scan(Initial.class, configuration.getScanPackages())
        .forEach(
            (c) -> {
              var key = InitialStateKey.fromInitialStateClass(c);
              initialStates.put(key, c);
            });
    this.initialStates = Collections.unmodifiableMap(initialStates);
  }

  @Override
  public <S, A> Reducer<S, A> getReducer(final ReducerKey<S, A> key) {

    var reducerClass = reducers.get(key);

    if (reducerClass == null) {
      throw new PipelineException("reducer not found for key: " + key);
    }

    return applicationContext.getBean(reducerClass);
  }

  @Override
  public <S> KeyStrategy getKeyStrategy(final Class<S> stateType) {
    return applicationContext.getBean(
        Objects.requireNonNull(keyStrategies.get(stateType), "keyStrategy"));
  }

  @Override
  public <S> List<StateGuard<S>> getStateGuards(Class<S> stateType) {

    List<StateGuard<S>> result = new ArrayList<>();

    var clss = stateGuardClasses.get(stateType);
    if (clss == null) {
      return result;
    }
    for (var cls : clss) {
      result.add((StateGuard<S>) applicationContext.getBean(cls));
    }

    return result;
  }

  @Override
  public Iterable<Class<?>> getStateTypes() {
    return this.stateTypes;
  }

  @Override
  public Iterable<ReducerKey<?, ?>> getReducerKeys() {
    return this.reducers.keySet();
  }

  @Override
  public <S> InitialState<S> getInitial(final InitialStateKey<S> key) {

    var initialStateClass = initialStates.get(key);

    if (initialStateClass == null) {
      throw new PipelineException("initial state not found for key: " + key);
    }

    return applicationContext.getBean(initialStateClass);
  }

  private static void registerStateGuards(
      Map<Class<?>, List<Class<? extends StateGuard>>> stateGuardClasses, Class<?> stateClass) {

    var ann = stateClass.getAnnotation(State.class);
    if (ann != null && ann.guards() != null) {
      stateGuardClasses.put(stateClass, Lists.newArrayList(ann.guards()));
    }
  }

  private static void registerKeyStrategies(
      Map<Class<?>, Class<? extends KeyStrategy>> keyStrategies, Class<?> stateClass) {

    var ann = stateClass.getAnnotation(State.class);
    if (ann != null) {
      keyStrategies.put(stateClass, Objects.requireNonNull(ann.keyStrategy(), "keyStrategy"));
    }
  }
}
