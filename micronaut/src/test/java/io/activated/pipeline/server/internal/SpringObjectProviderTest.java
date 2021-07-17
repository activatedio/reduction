package io.activated.pipeline.server.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;

import io.activated.pipeline.PipelineException;
import io.activated.pipeline.server.fixtures.Dummy1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
public class SpringObjectProviderTest {

  private SpringObjectProvider unit;

  @Mock private ApplicationContext applicationContext;

  @BeforeEach
  public void setUp() {

    unit = new SpringObjectProvider(applicationContext);
  }

  @Test
  public void get_notFound() {
    try {
      unit.get(Dummy1.class);
      fail("Expected PipelineException to be thrown");
    } catch (final PipelineException e) {
      assertThat(e).hasMessage("Bean not found for class: " + Dummy1.class);
    }
  }

  @Test
  public void get() {
    final var result = new Dummy1();
    when(applicationContext.getBean(Dummy1.class)).thenReturn(result);
    assertThat(unit.get(Dummy1.class)).isSameAs(result);
  }
}
