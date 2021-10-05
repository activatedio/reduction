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

@ExtendWith(MockitoExtension.class)
class DataFetcherFactoryImplTest {

  private final Class<DummyState> stateClass = DummyState.class;
  private final Class<DummyReducer> actionClass = DummyReducer.class;

  @Mock private Pipeline pipeline;

  private DataFetcherFactoryImpl unit;

  @BeforeEach
  public void setUp() {
    unit = new DataFetcherFactoryImpl(pipeline, sessionIdSupplier);
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
