package io.activated.pipeline.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.activated.pipeline.*;
import io.activated.pipeline.fixtures.DummyAction;
import io.activated.pipeline.fixtures.DummyState;
import javax.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
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
public class ValidatingPipelineTest {

  @Mock private Pipeline delegate;

  private ValidatingPipeline unit;
  private Context context;

  private DummyState state;

  @BeforeEach
  public void setUp() {

    context = new Context();
    state = new DummyState();
    unit = new ValidatingPipeline(Validation.buildDefaultValidatorFactory(), delegate);
  }

  private void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(delegate);
  }

  @Test
  public void get() {

    var result = new GetResult<DummyState>();
    result.setState(state);

    when(delegate.get(context, DummyState.class)).thenReturn(Mono.just(result));

    assertThat(Mono.from(unit.get(context, DummyState.class)).block().getState()).isSameAs(state);

    verifyNoMoreInteractions();
  }

  @Test
  public void set_valid() {

    var result = new SetResult<DummyState>();
    result.setState(state);

    var action = new DummyAction();

    when(delegate.set(context, DummyState.class, action)).thenReturn(Mono.just(result));

    assertThat(Mono.from(unit.set(context, DummyState.class, action)).block().getState())
        .isSameAs(state);

    verifyNoMoreInteractions();
  }

  @Test
  public void set_invalid_one() {

    var result = new SetResult<DummyState>();
    result.setState(state);

    var action = new DummyAction();
    action.setValue("123456");

    assertThatThrownBy(
            () -> Mono.from(unit.set(context, DummyState.class, action)).block().getState())
        .isInstanceOf(PipelineValidationException.class)
        .hasMessage("Validation failed: value: size must be between 0 and 5");

    verifyNoMoreInteractions();
  }

  @Test
  public void set_invalid_two() {

    var result = new SetResult<DummyState>();
    result.setState(state);

    var action = new DummyAction();
    action.setValue("123456");
    action.setValue2("123");

    assertThatThrownBy(
            () -> Mono.from(unit.set(context, DummyState.class, action)).block().getState())
        .isInstanceOf(PipelineValidationException.class)
        .hasMessage(
            "Validation failed: value2: size must be between 0 and 2, value: size must be between 0 and 5");

    verifyNoMoreInteractions();
  }
}
