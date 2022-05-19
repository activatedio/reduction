package io.activated.pipeline.micronaut;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import io.activated.pipeline.Context;
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

  private final Context context = new Context();

  @BeforeEach
  public void setUp() {
    unit =
        new GetDataFetcherImpl<DummyState>(pipeline, DummyState.class) {
          @Override
          protected Context getContext() {
            return context;
          }
        };
  }

  @Test
  public void get() throws Exception {
    final var result = new GetResult<DummyState>();
    when(pipeline.get(context, DummyState.class)).thenReturn(Mono.just(result));
    assertThat(Mono.fromFuture(unit.get(null)).block()).isEqualTo(result);
    verifyNoMoreInteractions(pipeline);
  }
}
