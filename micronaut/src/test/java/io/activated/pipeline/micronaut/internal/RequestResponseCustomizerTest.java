package io.activated.pipeline.micronaut.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import graphql.ExecutionInput;
import graphql.GraphQLContext;
import io.micronaut.http.HttpRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class RequestResponseCustomizerTest {

  @Mock private ExecutionInput executionInput;
  @Mock private GraphQLContext graphQLContext;
  @Mock private HttpRequest request;

  @Test
  public void customize() {

    var unit = new RequestResponseCustomizer();

    when(executionInput.getContext()).thenReturn(graphQLContext);

    var got = Mono.from(unit.customize(executionInput, request, null)).block();

    verify(graphQLContext).put(Constants.GRAPHQL_CONTEXT_REQUEST_KEY, request);

    assertThat(got).isSameAs(executionInput);

    Mockito.verifyNoMoreInteractions(executionInput, graphQLContext, request);
  }
}
