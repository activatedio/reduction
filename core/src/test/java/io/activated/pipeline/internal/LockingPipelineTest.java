package io.activated.pipeline.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.activated.base.ApplicationRuntimeException;
import io.activated.pipeline.*;
import io.activated.pipeline.fixtures.Dummy1;
import io.activated.pipeline.fixtures.DummyAction;
import io.activated.pipeline.repository.LockRepository;
import java.util.List;
import java.util.Map;
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
public class LockingPipelineTest {

  // TODO - Improve this unit test to capture the addition of the lock to the context

  private static final String LOCK_ATTRIBUTE_KEY = "Pipeline.Lock";

  @Mock private LockRepository lockRepository;

  @Mock private Pipeline delegate;

  private LockingPipeline unit;

  private String sessionId = "test-session-id";

  private Context context;

  private Lock lock;

  private GetResult<Dummy1> getResult;

  private SetResult<Dummy1> setResult;

  private DummyAction action;

  @BeforeEach
  public void setUp() {

    getResult = new GetResult<>();
    getResult.setState(new Dummy1());

    setResult = new SetResult<>();
    setResult.setState(new Dummy1());

    action = new DummyAction();

    context = new Context();
    context.setHeaders(Map.of(Constants.SESSION_ID_CONTEXT_KEY, List.of(sessionId)));

    lock = new Lock(sessionId);

    unit = new LockingPipeline(lockRepository, delegate);
  }

  private void verifyNoMoreInteractions() {

    Mockito.verifyNoMoreInteractions(lockRepository, delegate);
  }

  @Test
  public void get() {

    when(lockRepository.acquire(sessionId)).thenReturn(lock);
    when(delegate.get(context, Dummy1.class)).thenReturn(Mono.just(getResult));

    var got = Mono.from(unit.get(context, Dummy1.class)).block();

    assertThat(got).isSameAs(getResult);

    verify(lockRepository).release(lock);

    verifyNoMoreInteractions();
  }

  @Test
  public void get_withException() {

    var delegateContext = new Context();
    delegateContext.getAttributes().put(LOCK_ATTRIBUTE_KEY, lock);

    when(lockRepository.acquire(sessionId)).thenReturn(lock);
    when(delegate.get(delegateContext, Dummy1.class))
        .thenThrow(new ApplicationRuntimeException("test-exception"));

    assertThatThrownBy(() -> Mono.from(unit.get(context, Dummy1.class)).block())
        .isInstanceOf(ApplicationRuntimeException.class)
        .hasMessage("test-exception");

    // Ensure release is always called
    verify(lockRepository).release(lock);

    verifyNoMoreInteractions();
  }

  @Test
  public void set() {

    when(delegate.set(context, Dummy1.class, action)).thenReturn(Mono.just(setResult));

    var got = Mono.from(unit.set(context, Dummy1.class, action)).block();

    assertThat(got).isSameAs(setResult);

    verify(lockRepository).release(lock);

    verifyNoMoreInteractions();
  }

  @Test
  public void set_withException() {

    when(lockRepository.acquire(sessionId)).thenReturn(lock);
    when(delegate.set(context, Dummy1.class, action))
        .thenThrow(new ApplicationRuntimeException("test-exception"));

    assertThatThrownBy(() -> Mono.from(unit.set(context, Dummy1.class, action)).block())
        .isInstanceOf(ApplicationRuntimeException.class)
        .hasMessage("test-exception");

    // Ensure release is always called
    verify(lockRepository).release(lock);

    verifyNoMoreInteractions();
  }
}
