package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.*;
import io.activated.pipeline.Constants;
import io.activated.pipeline.env.SessionIdSupplier;
import io.micronaut.http.context.ServerRequestContext;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public class ContextPipelineImpl implements Pipeline {

  private final Pipeline delegate;
  private final SessionIdSupplier sessionIdSupplier;

  public ContextPipelineImpl(Pipeline delegate, SessionIdSupplier sessionIdSupplier) {
    this.delegate = delegate;
    this.sessionIdSupplier = sessionIdSupplier;
  }

  @Override
  public <S> Publisher<GetResult<S>> get(Context context, Class<S> stateType) {

    if (context != null) {
      throw new IllegalArgumentException("cannot pass in existing context");
    }

    return Mono.from(delegate.get(getContext(), stateType));
  }

  @Override
  public <S, A> Publisher<SetResult<S>> set(Context context, Class<S> stateType, A action) {

    if (context != null) {
      throw new IllegalArgumentException("cannot pass in existing context");
    }

    return Mono.from(delegate.set(getContext(), stateType, action));
  }

  private Context getContext() {

    var request = ServerRequestContext.currentRequest().get();
    var context = new Context();
    context.setHeaders(request.getHeaders().asMap());
    return context;
  }
}
