package io.activated.pipeline.micronaut;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Context;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.micronaut.fixtures.DummyState;
import io.activated.pipeline.micronaut.internal.Constants;
import io.micronaut.http.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class GetDataFetcherImplTest {

  private final Class<DummyState> stateClass = DummyState.class;

  @Mock private ContextFactory contextFactory;

  @Mock private Pipeline pipeline;

  @Mock private DataFetchingEnvironment environment;

  @Mock private GraphQLContext graphQLContext;

  @Mock private HttpRequest<?> request;

  private GetDataFetcherImpl<DummyState> unit;

  @BeforeEach
  public void setUp() {
    unit = new GetDataFetcherImpl<DummyState>(contextFactory, pipeline, DummyState.class);
  }

  @Test
  public void get() throws Exception {
    var context = new Context();
    final var result = new GetResult<DummyState>();
    when(environment.getContext()).thenReturn(graphQLContext);
    when(graphQLContext.get(Constants.GRAPHQL_CONTEXT_REQUEST_KEY)).thenReturn(request);
    when(contextFactory.create(request)).thenReturn(Mono.just(context));
    when(pipeline.get(context, DummyState.class)).thenReturn(Mono.just(result));
    assertThat(Mono.fromFuture(unit.get(environment)).block()).isEqualTo(result);
  }
}
