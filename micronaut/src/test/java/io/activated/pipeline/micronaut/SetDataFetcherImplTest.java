package io.activated.pipeline.micronaut;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.Maps;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Context;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import io.activated.pipeline.micronaut.fixtures.DummyAction;
import io.activated.pipeline.micronaut.fixtures.DummyExternalState;
import io.activated.pipeline.micronaut.fixtures.DummyInternalState;
import io.activated.pipeline.micronaut.internal.Constants;
import io.micronaut.http.HttpRequest;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class SetDataFetcherImplTest {

  private final Class<DummyInternalState> stateClass = DummyInternalState.class;
  private final Class<DummyAction> actionClass = DummyAction.class;
  @Mock private DataFetchingEnvironment environment;

  @Mock private ContextFactory contextFactory;

  @Mock private Pipeline pipeline;

  @Mock private GraphQLContext graphQLContext;

  @Mock private HttpRequest<?> request;

  @Mock private Function<DummyInternalState, DummyExternalState> mapper;

  private SetDataFetcherImpl<DummyInternalState, DummyExternalState, DummyAction> unit;

  @BeforeEach
  public void setUp() {

    unit = new SetDataFetcherImpl<>(contextFactory, pipeline, stateClass, actionClass, mapper);
  }

  @Test
  public void get() throws Exception {

    Map<String, Object> argument = Maps.newHashMap();
    argument.put("value", "valueA");

    var action = new DummyAction();
    action.setValue("valueA");

    var stateIn = new DummyInternalState();
    var stateOut = new DummyExternalState();

    var result = new SetResult<DummyInternalState>();
    result.setState(stateIn);

    var pubResult = Mono.just(result);

    var context = new Context();
    when(environment.getGraphQlContext()).thenReturn(graphQLContext);
    when(graphQLContext.get(Constants.GRAPHQL_CONTEXT_REQUEST_KEY)).thenReturn(request);
    when(contextFactory.create(request)).thenReturn(Mono.just(context));
    when(environment.getArgument("action")).thenReturn(argument);
    when(pipeline.set(context, DummyInternalState.class, action)).thenReturn(pubResult);
    when(mapper.apply(stateIn)).thenReturn(stateOut);

    var got = unit.get(environment);

    assertThat(got.get().getState()).isSameAs(stateOut);
  }
}
