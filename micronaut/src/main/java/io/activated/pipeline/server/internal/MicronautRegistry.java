package io.activated.pipeline.server.internal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.activated.pipeline.InitialState;
import io.activated.pipeline.PipelineException;
import io.activated.pipeline.Reducer;
import io.activated.pipeline.StateGuard;
import io.activated.pipeline.annotations.Operation;
import io.activated.pipeline.annotations.State;
import io.activated.pipeline.internal.InitialStateKey;
import io.activated.pipeline.internal.ReducerKey;
import io.activated.pipeline.internal.Registry;
import io.activated.pipeline.key.KeyStrategy;
import io.activated.pipeline.key.SessionKeyStrategy;
import io.activated.pipeline.server.MainRuntimeConfiguration;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.AnnotationUtil;
import io.micronaut.core.io.scan.ClassPathAnnotationScanner;
import io.micronaut.inject.qualifiers.Qualifiers;
import java.util.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MicronautRegistry implements Registry {

  private final ApplicationContext applicationContext;

  private static final KeyStrategy DEFAULT_KEY_STRATEGY = new SessionKeyStrategy(new SessionIdSupplierImpl());

  private final List<Class<?>> stateTypes;
  private final List<ReducerKey<?,?>> reducerKeys;
  private final Map<Class<?>, List<Class<? extends StateGuard<?>>>> stateGuardClasses;

  @Inject
  public MicronautRegistry(final ApplicationContext applicationContext, final MainRuntimeConfiguration configuration) {
    this.applicationContext = applicationContext;

    var scanner = new ClassPathAnnotationScanner();

    List<Class<?>> stateTypes = Lists.newArrayList();
    Map<Class<?>, List<Class<? extends StateGuard<?>>>> stateGuardClasses = Maps.newHashMap();
    scanner.scan(State.class, configuration.getScanPackages()).forEach((c) -> {
      stateTypes.add(c);
      registerStateGuards(stateGuardClasses, c);
    });
    this.stateTypes = Collections.unmodifiableList(stateTypes);
    this.stateGuardClasses = Collections.unmodifiableMap(stateGuardClasses);

    Set<Class<? extends StateGuard<?>>> guards = Sets.newHashSet();
    this.stateGuardClasses.values().forEach(guards::addAll);

    guards.forEach(g -> {
      applicationContext.registerSingleton(applicationContext.createBean(g));
    });


    List<ReducerKey<?, ?>> reducerKeys = Lists.newArrayList();
    scanner.scan(Operation.class, configuration.getScanPackages()).forEach((c) -> {
      var key = ReducerKey.fromReducerClass(c);
      reducerKeys.add(key);
      var bean = applicationContext.createBean(c);
      Class type = bean.getClass();
      applicationContext.registerSingleton(type, bean, Qualifiers.byTypeArguments(key.getStateType(),
          key.getActionType()));
    });
    this.reducerKeys = Collections.unmodifiableList(reducerKeys);

  }

  @Override
  public <S, A> Reducer<S, A> getReducer(final ReducerKey<S, A> key) {
    return applicationContext.getBean(Reducer.class, Qualifiers.byTypeArguments(key.getStateType(), key.getActionType()));
  }

  @Override
  public <S> KeyStrategy getKeyStrategy(final Class<S> stateType) {
    return DEFAULT_KEY_STRATEGY;
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
    return this.reducerKeys;
  }

  @Override
  public <S> InitialState<S> getInitial(final InitialStateKey<S> key) {
    return applicationContext.getBean(InitialState.class, Qualifiers.byTypeArguments(key.getStateType()));
  }

  private static void registerStateGuards(
      Map<Class<?>, List<Class<? extends StateGuard<?>>>> stateGuardClasses, Class<?> stateClass) {

    var ann = stateClass.getAnnotation(State.class);
    if (ann != null && ann.guards() != null) {
      stateGuardClasses.put(stateClass, Lists.newArrayList(ann.guards()));
    }
  }
}
