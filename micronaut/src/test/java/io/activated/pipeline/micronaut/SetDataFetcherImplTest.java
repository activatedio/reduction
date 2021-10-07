package io.activated.pipeline.micronaut;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.Maps;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import io.activated.pipeline.micronaut.fixtures.DummyAction;
import io.activated.pipeline.micronaut.fixtures.DummyState;
import io.reactivex.internal.operators.flowable.FlowableScan;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SetDataFetcherImplTest {

  private final Class<DummyState> stateClass = DummyState.class;
  private final Class<DummyAction> actionClass = DummyAction.class;
  @Mock private DataFetchingEnvironment environment;
  @Mock private Pipeline pipeline;

  private SetDataFetcherImpl<DummyState, DummyAction> unit;

  @BeforeEach
  public void setUp() {

    unit = new SetDataFetcherImpl<DummyState, DummyAction>(pipeline, stateClass, actionClass);
  }

  @Test
  public void get() throws Exception {

    Map<String, Object> argument = Maps.newHashMap();
    argument.put("value", "valueA");

    var action = new DummyAction();
    action.setValue("valueA");

    var result = new SetResult<DummyState>();
    result.setState(new DummyState());

    var pubResult = FlowableScan.just(result);

    when(environment.getArgument("action")).thenReturn(argument);
    when(pipeline.set(null, DummyState.class, action)).thenReturn(pubResult);

    var got = unit.get(environment);

    assertThat(got.get()).isSameAs(result);
  }
}
