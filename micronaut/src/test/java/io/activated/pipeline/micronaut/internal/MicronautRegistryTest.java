package io.activated.pipeline.micronaut.internal;

import com.google.common.collect.Sets;
import io.activated.pipeline.PipelineException;
import io.activated.pipeline.env.SessionIdSupplier;
import io.activated.pipeline.internal.InitialStateKey;
import io.activated.pipeline.internal.ReducerKey;
import io.activated.pipeline.key.SessionKeyStrategy;
import io.activated.pipeline.micronaut.MainRuntimeConfiguration;
import io.activated.pipeline.micronaut.fixtures.*;
import io.micronaut.context.ApplicationContext;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@MicronautTest
public class MicronautRegistryTest {

  private MicronautRegistry unit;

  @Inject private ApplicationContext applicationContext;

  private SessionIdSupplier  sessionIdSupplier;

  private final ReducerKey<DummyState, DummyAction> reducerKey =
      ReducerKey.create(DummyState.class, DummyAction.class);

  private final InitialStateKey<DummyState> initialStateKey =
      InitialStateKey.create(DummyState.class);

  @BeforeEach
  public void setUp() {

    var config = new MainRuntimeConfiguration();
    config.setScanPackages(new String[] {"io.activated.pipeline.micronaut.fixtures"});

    sessionIdSupplier = new SessionIdSupplier() {
      @Override
      public String get() {
        return "test-session-id";
      }
    };

    unit = new MicronautRegistry(applicationContext, sessionIdSupplier, config);
  }

  @Test
  public void constructor_emptyScanPackages() {

    var config = new MainRuntimeConfiguration();
    config.setScanPackages(new String[]{});
    // Should not throw an exception
    new MicronautRegistry(applicationContext, sessionIdSupplier, config);
  }

  @Test
  public void constructor_nullConfiguration() {

    assertThatThrownBy(() -> {
      new MicronautRegistry(applicationContext, sessionIdSupplier, null);
    }).isInstanceOf(NullPointerException.class).hasMessage("Argument [configuration] cannot be null");
  }

  @Test
  public void constructor_nullScanPackages() {

    assertThatThrownBy(() -> {
      var config = new MainRuntimeConfiguration();
      new MicronautRegistry(applicationContext, sessionIdSupplier, config);
    }).isInstanceOf(NullPointerException.class).hasMessage("Argument [configuration.scanPackages] cannot be null");
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
    assertThat(unit.getKeyStrategy(Dummy1.class)).isInstanceOf(SessionKeyStrategy.class);
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
