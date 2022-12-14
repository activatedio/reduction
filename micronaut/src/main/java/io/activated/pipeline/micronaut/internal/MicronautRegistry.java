package io.activated.pipeline.micronaut.internal;

import static org.reflections.scanners.Scanners.*;

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
import io.activated.pipeline.key.PrincipalSessionKeyUpgradeStrategy;
import io.activated.pipeline.key.SessionKeyStrategy;
import io.activated.pipeline.micronaut.MicronautPipelineConfiguration;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.ArgumentUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.*;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

@Singleton
public class MicronautRegistry implements Registry {

  private final ApplicationContext applicationContext;

  private final Set<Class<?>> stateTypes;
  private final Map<ReducerKey<?, ?>, Class<? extends Reducer>> reducers;
  private final Map<InitialStateKey<?>, Class<? extends InitialState>> initialStates;
  private final Map<Class<?>, List<Class<? extends StateGuard>>> stateGuardClasses;

  private KeyStrategy defaultKeyStrategy;

  @Inject
  public MicronautRegistry(
      final ApplicationContext applicationContext,
      final MicronautPipelineConfiguration configuration) {

    // TOOD - Make this injectable one day
    this.defaultKeyStrategy = new PrincipalSessionKeyUpgradeStrategy(new SessionKeyStrategy());
    this.applicationContext = applicationContext;

    ArgumentUtils.requireNonNull("configuration", configuration);
    ArgumentUtils.requireNonNull("configuration.scanPackages", configuration.getScanPackages());

    Set<Class<?>> stateTypes = Sets.newHashSet();
    Map<Class<?>, List<Class<? extends StateGuard>>> stateGuardClasses = Maps.newHashMap();

    Reflections scanner =
        new Reflections(new ConfigurationBuilder().forPackages(configuration.getScanPackages()));

    scanner
        .get(TypesAnnotated.with(State.class).asClass())
        .forEach(
            (c) -> {
              stateTypes.add(c);
              registerStateGuards(stateGuardClasses, c);
            });

    this.stateTypes = Collections.unmodifiableSet(stateTypes);
    this.stateGuardClasses = Collections.unmodifiableMap(stateGuardClasses);

    final Set<Class<? extends StateGuard>> guards = Sets.newHashSet();
    this.stateGuardClasses.values().forEach(guards::addAll);

    final Map<ReducerKey<?, ?>, Class<? extends Reducer>> reducers = Maps.newHashMap();

    scanner
        .get(TypesAnnotated.with(Operation.class).asClass())
        .forEach(
            (c) -> {
              var key = ReducerKey.fromReducerClass(c);
              reducers.put(key, (Class<? extends Reducer>) c);
            });

    this.reducers = Collections.unmodifiableMap(reducers);

    final Map<InitialStateKey<?>, Class<? extends InitialState<?>>> initialStates =
        Maps.newHashMap();

    scanner
        .get(TypesAnnotated.with(Initial.class).asClass())
        .forEach(
            (c) -> {
              var key = InitialStateKey.fromInitialStateClass(c);
              initialStates.put(key, (Class<? extends InitialState<?>>) c);
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
    return defaultKeyStrategy;
  }

  @Override
  public <S> List<StateGuard<S>> getStateGuards(Class<S> stateType) {

    List<StateGuard<S>> result = new ArrayList<>();

    var clss = stateGuardClasses.get(stateType);
    if (clss == null) {
      return result;
    }
    for (var cls : clss) {
      result.add(applicationContext.getBean(cls));
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
}
