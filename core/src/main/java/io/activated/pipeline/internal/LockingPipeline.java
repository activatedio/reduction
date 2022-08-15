package io.activated.pipeline.internal;

import io.activated.pipeline.*;
import io.activated.pipeline.repository.LockRepository;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class LockingPipeline implements Pipeline {

  private final LockRepository lockRepository;
  private final Pipeline delegate;

  public LockingPipeline(LockRepository lockRepository, Pipeline delegate) {
    this.lockRepository = lockRepository;
    this.delegate = delegate;
  }

  @Override
  public <S> Publisher<GetResult<S>> get(Context context, Class<S> stateType) {
    return Mono.using(
            () -> lockRepository.acquire(getSessionId(context)),
            l -> Mono.from(delegate.get(context, stateType)),
            lockRepository::release)
        .subscribeOn(Schedulers.boundedElastic());
  }

  @Override
  public <S, A> Publisher<SetResult<S>> set(Context context, Class<S> stateType, A action) {
    return Mono.using(
            () -> lockRepository.acquire(getSessionId(context)),
            l -> Mono.from(delegate.set(context, stateType, action)),
            lockRepository::release)
        .subscribeOn(Schedulers.boundedElastic());
  }

  private String getSessionId(Context context) {

    var sessionId = context.getHeaders().get(Constants.SESSION_ID_CONTEXT_KEY);
    if (sessionId == null || sessionId.size() != 1) {
      throw new IllegalStateException("pipeline-session-id not provided in header");
    }
    return sessionId.get(0);
  }
}
