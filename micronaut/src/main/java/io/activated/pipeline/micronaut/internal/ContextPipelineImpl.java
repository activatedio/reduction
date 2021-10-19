package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.*;
import io.activated.pipeline.env.SessionIdSupplier;
import io.micronaut.http.context.ServerRequestContext;
import java.util.List;
import java.util.TreeMap;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class ContextPipelineImpl implements Pipeline {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContextPipelineImpl.class);

  private final Pipeline delegate;

  public ContextPipelineImpl(Pipeline delegate) {
    this.delegate = delegate;
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
    var headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
    // TODO - How to test both types of headers
    headers.putAll(request.getHeaders().asMap());
    context.setHeaders(headers);
    LOGGER.info("using context: " + context);
    return context;
  }
}
