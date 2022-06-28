package io.activated.pipeline.micronaut.internal;


import io.activated.pipeline.Context;
import io.activated.pipeline.micronaut.ContextBuilder;
import io.micronaut.http.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ContextFactoryImplTest {

  @Mock
  private ContextBuilder contextBuilder1, contextBuilder2;

  @Mock
  private HttpRequest<?> request;

  private final Context context = new Context();

  private ContextFactoryImpl makeUnit(List<ContextBuilder> contextBuilders) {

    return new ContextFactoryImpl(contextBuilders) {
      @Override
      protected ContextUtils.Result initial() {
        return new ContextUtils.Result(request, context);
      }
    };
  }

  private void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(request, contextBuilder1, contextBuilder2);
  }

  @Test
  public void create_empty() {

    assertThat(makeUnit(List.of()).create().block()).isEqualTo(context);

    verifyNoMoreInteractions();
  }

  @Test
  public void create_both() {

    when(contextBuilder1.order()).thenReturn(10);
    when(contextBuilder1.build(request, context)).thenReturn(Mono.just(context));
    when(contextBuilder2.order()).thenReturn(20);
    when(contextBuilder2.build(request, context)).thenReturn(Mono.just(context));

    assertThat(makeUnit(List.of(contextBuilder1, contextBuilder2)).create().block()).isEqualTo(context);

    // Reverse order but the sort fixes that
    var inOrder = Mockito.inOrder(contextBuilder2, contextBuilder1);

    inOrder.verify(contextBuilder1).build(request, context);
    inOrder.verify(contextBuilder2).build(request, context);

    verifyNoMoreInteractions();
  }
}