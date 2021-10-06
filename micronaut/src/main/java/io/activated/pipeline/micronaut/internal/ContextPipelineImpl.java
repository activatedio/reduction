package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.Constants;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import io.activated.pipeline.env.SessionIdSupplier;
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
    public <S> Publisher<GetResult<S>> get(Class<S> stateType) {
        return Mono.from(delegate.get(stateType)).contextWrite(ctx -> {
            if (ctx.get(Constants.SESSION_ID_CONTEXT_KEY) == null) {
                return ctx.put(Constants.SESSION_ID_CONTEXT_KEY, sessionIdSupplier.get());
            } else {
                return ctx;
            }
        });
    }

    @Override
    public <S, A> Publisher<SetResult<S>> set(Class<S> stateType, A action) {
        return Mono.from(delegate.set(stateType, action)).contextWrite(ctx -> {
            if (ctx.get(Constants.SESSION_ID_CONTEXT_KEY) == null) {
                return ctx.put(Constants.SESSION_ID_CONTEXT_KEY, sessionIdSupplier.get());
            } else {
                return ctx;
            }
        });
    }
}
