package io.activated.pipeline.micronaut;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Context;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.micronaut.fixtures.DummyExternalState;
import io.activated.pipeline.micronaut.fixtures.DummyInternalState;
import io.activated.pipeline.micronaut.internal.Constants;
import io.micronaut.http.HttpRequest;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class GetDataFetcherImplTest {

  private final Class<DummyInternalState> stateClass = DummyInternalState.class;

  @Mock private ContextFactory contextFactory;

  @Mock private Pipeline pipeline;

  @Mock private DataFetchingEnvironment environment;

  @Mock private GraphQLContext graphQLContext;

  @Mock private HttpRequest<?> request;

  @Mock private Function<DummyInternalState, DummyExternalState> mapper;

  private GetDataFetcherImpl<DummyInternalState, DummyExternalState> unit;

  @BeforeEach
  public void setUp() {
    unit = new GetDataFetcherImpl<>(contextFactory, pipeline, DummyInternalState.class, mapper);
  }

  @Test
  public void get() throws Exception {
    var context = new Context();
    var stateIn = new DummyInternalState();
    var stateOut = new DummyExternalState();
    var result = new GetResult<DummyInternalState>();
    result.setState(stateIn);
    when(environment.getGraphQlContext()).thenReturn(graphQLContext);
    when(graphQLContext.get(Constants.GRAPHQL_CONTEXT_REQUEST_KEY)).thenReturn(request);
    when(contextFactory.create(request)).thenReturn(Mono.just(context));
    when(pipeline.get(context, DummyInternalState.class)).thenReturn(Mono.just(result));
    when(mapper.apply(stateIn)).thenReturn(stateOut);
    assertThat(Mono.fromFuture(unit.get(environment)).block().getState()).isSameAs(stateOut);
  }
}
