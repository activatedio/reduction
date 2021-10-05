package io.activated.pipeline.micronaut;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.micronaut.fixtures.DummyState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class GetDataFetcherImplTest {

  private final Class<DummyState> stateClass = DummyState.class;
  @Mock private Pipeline pipeline;

  private GetDataFetcherImpl<DummyState> unit;

  @BeforeEach
  public void setUp() {
    unit = new GetDataFetcherImpl<DummyState>(pipeline, sessionIdSupplier, DummyState.class);
  }

  @Test
  public void get() throws Exception {
    final var result = new GetResult<DummyState>();
    when(pipeline.get(DummyState.class)).thenReturn(Mono.just(result));
    assertThat(Mono.fromFuture(unit.get(null)).block()).isEqualTo(result);
  }
}
