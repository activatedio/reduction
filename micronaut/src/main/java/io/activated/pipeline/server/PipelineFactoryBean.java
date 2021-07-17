package io.activated.pipeline.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.activated.pipeline.InitialState;
import io.activated.pipeline.Reducer;
import io.activated.pipeline.StateGuard;
import io.activated.pipeline.annotations.Initial;
import io.activated.pipeline.annotations.Operation;
import io.activated.pipeline.annotations.State;
import io.activated.pipeline.internal.InitialStateKey;
import io.activated.pipeline.internal.ReducerKey;
import io.activated.pipeline.server.internal.InitialStateClassesSupplier;
import io.activated.pipeline.server.internal.ReducerClassesSupplier;
import io.activated.pipeline.server.internal.StateClassesSupplier;
import io.activated.pipeline.server.internal.StateGuardClassesSupplier;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.type.filter.AnnotationTypeFilter;

@Configuration
public class PipelineFactoryBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(PipelineFactoryBean.class);

  @Bean
  static BeanDefinitionRegistryPostProcessor pipelinePostProcessor(
      final ConfigurableEnvironment environment) {
    return new BeanDefinitionRegistryPostProcessor() {

      @Override
      public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory)
          throws BeansException {}

      @Override
      public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry)
          throws BeansException {

        String[] basePackages =
            environment.getProperty("activated.pipeline.packages", String[].class);

        if (basePackages == null) {
          basePackages = new String[0];
        }

        final var operationScanner = new ClassPathScanningCandidateComponentProvider(false);
        operationScanner.addIncludeFilter(new AnnotationTypeFilter(Operation.class));

        final var initialScanner = new ClassPathScanningCandidateComponentProvider(false);
        initialScanner.addIncludeFilter(new AnnotationTypeFilter(Initial.class));

        final var stateScanner = new ClassPathScanningCandidateComponentProvider(false);
        stateScanner.addIncludeFilter(new AnnotationTypeFilter(State.class));

        final var definitions = new HashSet<BeanDefinition>();
        final var stateDefinitions = new HashSet<BeanDefinition>();
        final var operationDefinitions = new HashSet<BeanDefinition>();
        final var initialDefinitions = new HashSet<BeanDefinition>();

        for (final var basePackage : basePackages) {
          operationDefinitions.addAll(operationScanner.findCandidateComponents(basePackage));
          stateDefinitions.addAll(stateScanner.findCandidateComponents(basePackage));
          initialDefinitions.addAll(initialScanner.findCandidateComponents(basePackage));
          definitions.addAll(operationDefinitions);
          definitions.addAll(initialDefinitions);
        }

        for (final var definition : definitions) {
          LOGGER.info("Found qualifying bean definition: {}", definition);
          registry.registerBeanDefinition(definition.getBeanClassName(), definition);
        }

        final Set<Class<?>> stateClasses = Sets.newHashSet();
        final Map<ReducerKey<?, ?>, Class<? extends Reducer<?, ?>>> reducerClasses =
            Maps.newHashMap();
        final Map<InitialStateKey<?>, Class<? extends InitialState<?>>> initialClasses =
            Maps.newHashMap();
        final Map<Class<?>, List<Class<? extends StateGuard<?>>>> stateGuardClasses =
            Maps.newHashMap();

        for (final var state : stateDefinitions) {
          LOGGER.info("Found qualifying state class definition: {}", state);
          try {
            var stateClass = Class.forName(state.getBeanClassName());
            registerStateGuards(stateGuardClasses, stateClass);
            stateClasses.add(stateClass);
          } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
          }
        }

        for (final var operation : operationDefinitions) {
          LOGGER.info("Found qualifying reducer class definition: {}", operation);
          try {
            final var reducerClass =
                (Class<Reducer<?, ?>>) Class.forName(operation.getBeanClassName());
            reducerClasses.put(ReducerKey.fromReducerClass(reducerClass), reducerClass);
          } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
          }
        }

        for (final var initial : initialDefinitions) {
          LOGGER.info("Found qualifying initial state class definition: {}", initial);
          try {
            final var initialClass =
                (Class<InitialState<?>>) Class.forName(initial.getBeanClassName());
            initialClasses.put(InitialStateKey.fromInitialStateClass(initialClass), initialClass);
          } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
          }
        }

        registry.registerBeanDefinition(
            "stateClassesSupplier",
            new RootBeanDefinition(
                StateClassesSupplier.class,
                () -> {
                  return () -> {
                    return stateClasses;
                  };
                }));

        registry.registerBeanDefinition(
            "reducerClassesSupplier",
            new RootBeanDefinition(
                ReducerClassesSupplier.class,
                () -> {
                  return () -> {
                    return reducerClasses;
                  };
                }));

        registry.registerBeanDefinition(
            "initialStateClassesSupplier",
            new RootBeanDefinition(
                InitialStateClassesSupplier.class,
                () -> {
                  return () -> {
                    return initialClasses;
                  };
                }));

        registry.registerBeanDefinition(
            "stateGuardClassesSupplier",
            new RootBeanDefinition(
                StateGuardClassesSupplier.class,
                () -> {
                  return () -> {
                    return stateGuardClasses;
                  };
                }));
      }
    };
  }

  private static void registerStateGuards(
      Map<Class<?>, List<Class<? extends StateGuard<?>>>> stateGuardClasses, Class<?> stateClass) {

    var annotation = AnnotationUtils.findAnnotation(stateClass, State.class);
    if (annotation != null && annotation.guards() != null) {
      stateGuardClasses.put(stateClass, Lists.newArrayList(annotation.guards()));
    }
  }
}
