package io.activated.pipeline.internal;

import io.activated.pipeline.*;
import io.activated.pipeline.repository.LockRepository;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class LockingPipeline implements Pipeline {

  private static final String LOCK_ATTRIBUTE_KEY = "Pipeline.Lock";

  private final LockRepository lockRepository;
  private final Pipeline delegate;

  public LockingPipeline(LockRepository lockRepository, Pipeline delegate) {
    this.lockRepository = lockRepository;
    this.delegate = delegate;
  }

  @Override
  public <S> Publisher<GetResult<S>> get(Context context, Class<S> stateType) {
    return Mono.using(
            () -> acquireLock(context),
            l -> {
              context.getAttributes().put(LOCK_ATTRIBUTE_KEY, l);
              return Mono.from(delegate.get(context, stateType));
            },
            this::releaseLock)
        .subscribeOn(Schedulers.boundedElastic());
  }

  @Override
  public <S, A> Publisher<SetResult<S>> set(Context context, Class<S> stateType, A action) {
    return Mono.using(
            () -> acquireLock(context),
            l -> {
              context.getAttributes().put(LOCK_ATTRIBUTE_KEY, l);
              return Mono.from(delegate.set(context, stateType, action));
            },
            this::releaseLock)
        .subscribeOn(Schedulers.boundedElastic());
  }

  private Lock acquireLock(Context context) {
    if (context.getAttributes().containsKey(LOCK_ATTRIBUTE_KEY)) {
      var l = (Lock) context.getAttributes().get(LOCK_ATTRIBUTE_KEY);
      l.incrementNesting();
      return l;
    } else {
      return lockRepository.acquire(getSessionId(context));
    }
  }

  private String getSessionId(Context context) {

    var sessionId = context.getHeaders().get(Constants.SESSION_ID_CONTEXT_KEY);
    if (sessionId == null || sessionId.size() != 1) {
      throw new IllegalStateException("pipeline-session-id not provided in header");
    }
    return sessionId.get(0);
  }

  private void releaseLock(Lock lock) {

    if (lock.getNesting() > 0) {
      lock.decrementNesting();
    } else {
      lockRepository.release(lock);
    }
  }
}
