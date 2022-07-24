package io.activated.pipeline.micronaut;

import static org.assertj.core.api.Assertions.assertThat;

import io.activated.pipeline.Pipeline;
import io.activated.pipeline.micronaut.fixtures.DummyReducer;
import io.activated.pipeline.micronaut.fixtures.DummyState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class DataFetcherFactoryImplTest {

  private final Class<DummyState> stateClass = DummyState.class;
  private final Class<DummyReducer> actionClass = DummyReducer.class;

  @Mock private ContextFactory contextFactory;

  @Mock private Pipeline pipeline;

  private DataFetcherFactoryImpl unit;

  @BeforeEach
  public void setUp() {
    unit = new DataFetcherFactoryImpl(contextFactory, pipeline);
  }

  @Test
  public void getGetDataFetcher() {
    var got = unit.getGetDataFetcher(stateClass);
    assertThat(got).isInstanceOf(GetDataFetcherImpl.class);
  }

  @Test
  public void getSetDataFetcher() {
    var got = unit.getSetDataFetcher(stateClass, actionClass);
    assertThat(got).isInstanceOf(SetDataFetcherImpl.class);
  }
}
