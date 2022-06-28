package io.activated.pipeline.micronaut;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.activated.pipeline.Context;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.micronaut.fixtures.DummyExternalState;
import io.activated.pipeline.micronaut.fixtures.DummyInternalState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class ExportableGetDataFetcherImplTest {

  private final Class<DummyInternalState> stateClass = DummyInternalState.class;
  @Mock
  private ContextFactory contextFactory;
  @Mock private Pipeline pipeline;

  private ExportableGetDataFetcherImpl<DummyInternalState, DummyExternalState> unit;

  private final Context context = new Context();

  @BeforeEach
  public void setUp() {
    unit =
        new ExportableGetDataFetcherImpl<>(contextFactory, pipeline, stateClass) ;
  }

  @Test
  public void get() throws Exception {

    final var value = "test-1";
    final var internal = new DummyInternalState();
    internal.setInternalValue(value);

    final var external = internal.export();

    final var intermediate = new GetResult<DummyInternalState>();
    intermediate.setState(internal);

    final var result = new GetResult<DummyExternalState>();
    result.setState(external);

    when(pipeline.get(context, DummyInternalState.class)).thenReturn(Mono.just(intermediate));
    assertThat(Mono.fromFuture(unit.get(null)).block()).isEqualTo(result);
    verifyNoMoreInteractions(contextFactory, pipeline);
  }
}
