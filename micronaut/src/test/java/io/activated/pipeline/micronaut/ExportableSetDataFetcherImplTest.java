package io.activated.pipeline.micronaut;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.Maps;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Context;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import io.activated.pipeline.micronaut.fixtures.DummyAction;
import io.activated.pipeline.micronaut.fixtures.DummyExternalState;
import io.activated.pipeline.micronaut.fixtures.DummyInternalState;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class ExportableSetDataFetcherImplTest {

  private final Class<DummyInternalState> stateClass = DummyInternalState.class;
  private final Class<DummyAction> actionClass = DummyAction.class;
  @Mock private DataFetchingEnvironment environment;
  @Mock private Pipeline pipeline;

  private final Context context = new Context();

  private ExportableSetDataFetcherImpl<DummyInternalState, DummyExternalState, DummyAction> unit;

  @BeforeEach
  public void setUp() {

    unit =
        new ExportableSetDataFetcherImpl<>(pipeline, stateClass, actionClass) {
          @Override
          protected Context getContext() {
            return context;
          }
        };
  }

  @Test
  public void get() throws Exception {

    Map<String, Object> argument = Maps.newHashMap();
    argument.put("value", "valueA");

    var action = new DummyAction();
    action.setValue("valueA");

    var value = "test-1";

    var internal = new DummyInternalState();
    internal.setInternalValue(value);

    var external = internal.export();

    var intermediate = new SetResult<DummyInternalState>();
    intermediate.setState(internal);

    var result = new SetResult<DummyExternalState>();

    result.setState(external);

    when(environment.getArgument("action")).thenReturn(argument);
    when(pipeline.set(context, DummyInternalState.class, action))
        .thenReturn(Mono.just(intermediate));

    var got = unit.get(environment);

    assertThat(got.get()).isEqualTo(result);

    verifyNoMoreInteractions(pipeline);
  }
}
