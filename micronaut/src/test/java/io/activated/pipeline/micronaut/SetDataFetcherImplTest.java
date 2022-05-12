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
import io.activated.pipeline.micronaut.fixtures.DummyState;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class SetDataFetcherImplTest {

  private final Class<DummyState> stateClass = DummyState.class;
  private final Class<DummyAction> actionClass = DummyAction.class;
  @Mock private DataFetchingEnvironment environment;
  @Mock private Pipeline pipeline;

  private final Context context = new Context();

  private SetDataFetcherImpl<DummyState, DummyAction> unit;

  @BeforeEach
  public void setUp() {

    unit =
        new SetDataFetcherImpl<DummyState, DummyAction>(pipeline, stateClass, actionClass) {
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

    var result = new SetResult<DummyState>();
    result.setState(new DummyState());

    var pubResult = Mono.just(result);

    when(environment.getArgument("action")).thenReturn(argument);
    when(pipeline.set(context, DummyState.class, action)).thenReturn(pubResult);

    var got = unit.get(environment);

    assertThat(got.get()).isSameAs(result);

    verifyNoMoreInteractions(pipeline);
  }
}
