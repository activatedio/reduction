package io.activated.pipeline.micronaut.internal;

import static org.assertj.core.api.Assertions.*;

import com.google.common.collect.Sets;
import io.activated.pipeline.PipelineException;
import io.activated.pipeline.internal.InitialStateKey;
import io.activated.pipeline.internal.ReducerKey;
import io.activated.pipeline.key.PrincipalSessionKeyUpgradeStrategy;
import io.activated.pipeline.micronaut.StubMicronautPipelineConfiguration;
import io.activated.pipeline.micronaut.fixtures.*;
import io.micronaut.context.ApplicationContext;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import javax.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MicronautTest
public class MicronautRegistryTest {

  private MicronautRegistry unit;

  @Inject private ApplicationContext applicationContext;

  private final ReducerKey<DummyState, DummyAction> reducerKey =
      ReducerKey.create(DummyState.class, DummyAction.class);

  private final InitialStateKey<DummyState> initialStateKey =
      InitialStateKey.create(DummyState.class);

  @BeforeEach
  public void setUp() {

    var config =
        new StubMicronautPipelineConfiguration() {
          @Override
          public @NotNull String[] getScanPackages() {
            return new String[] {"io.activated.pipeline.micronaut.fixtures"};
          }
        };

    unit = new MicronautRegistry(applicationContext, config);
  }

  @Test
  public void constructor_emptyScanPackages() {

    var config =
        new StubMicronautPipelineConfiguration() {
          @Override
          public @NotNull String[] getScanPackages() {
            return new String[] {};
          }
        };

    // Should not throw an exception
    new MicronautRegistry(applicationContext, config);
  }

  @Test
  public void constructor_nullConfiguration() {

    assertThatThrownBy(
            () -> {
              new MicronautRegistry(applicationContext, null);
            })
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Argument [configuration] cannot be null");
  }

  @Test
  public void constructor_nullScanPackages() {

    assertThatThrownBy(
            () -> {
              var config = new StubMicronautPipelineConfiguration();
              new MicronautRegistry(applicationContext, config);
            })
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Argument [configuration.scanPackages] cannot be null");
  }

  @Test
  public void getReducer_NotFound() {
    var key = ReducerKey.create(String.class, String.class);
    assertThat(catchThrowable(() -> unit.getReducer(key)))
        .isInstanceOf(PipelineException.class)
        .hasMessage("reducer not found for key: " + key);
  }

  @Test
  public void getReducer() {
    final var result = new DummyReducer();
    assertThat(unit.getReducer(reducerKey)).isInstanceOf(DummyReducer.class);
  }

  @Test
  public void getReducerKeys() {
    assertThat(unit.getReducerKeys()).isEqualTo(Sets.newHashSet(reducerKey));
  }

  @Test
  public void getKeyStrategy() {
    assertThat(unit.getKeyStrategy(Dummy1.class))
        .isInstanceOf(PrincipalSessionKeyUpgradeStrategy.class);
  }

  @Test
  public void getStateTypes() {
    assertThat(unit.getStateTypes()).isEqualTo(Sets.newHashSet(DummyState.class));
  }

  @Test
  public void getInitial_NotFound() {
    var key = InitialStateKey.create(String.class);
    assertThat(catchThrowable(() -> unit.getInitial(key)))
        .isInstanceOf(PipelineException.class)
        .hasMessage("initial state not found for key: " + key);
  }

  @Test
  public void getInitial() {
    assertThat(unit.getInitial(initialStateKey)).isInstanceOf(DummyInitialState.class);
  }

  @Test
  public void getStateGuards() {
    var got = unit.getStateGuards(DummyState.class);
    assertThat(got).hasSize(2);
    assertThat(got.get(0)).isInstanceOf(DummyStateGuard1.class);
    assertThat(got.get(1)).isInstanceOf(DummyStateGuard2.class);
  }

  @Test
  public void getStateGuards_notFound() {
    assertThat(unit.getStateGuards(String.class)).isEmpty();
  }
}
