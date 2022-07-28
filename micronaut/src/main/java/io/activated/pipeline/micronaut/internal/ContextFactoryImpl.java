package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.Context;
import io.activated.pipeline.micronaut.ContextBuilder;
import io.activated.pipeline.micronaut.ContextFactory;
import io.micronaut.http.HttpRequest;
import jakarta.inject.Singleton;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@Singleton
public class ContextFactoryImpl implements ContextFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContextFactoryImpl.class);

  private final List<ContextBuilder> contextBuilders;

  public ContextFactoryImpl(List<ContextBuilder> contextBuilders) {
    this.contextBuilders =
        contextBuilders.stream()
            .sorted(Comparator.comparingInt(ContextBuilder::order))
            .collect(Collectors.toList());
  }

  @Override
  public Mono<Context> create(HttpRequest<?> request) {

    var ctx = initial(request);

    var m = Mono.just(ctx);

    for (var cb : contextBuilders) {
      m = m.flatMap(_ctx -> cb.build(request, _ctx));
    }

    return m.doOnNext(_ctx -> LOGGER.info("using context {}", _ctx));
  }

  protected Context initial(HttpRequest<?> request) {

    var context = new Context();
    context.getHeaders().putAll(request.getHeaders().asMap());
    context.getAttributes().putAll(request.getAttributes().asMap());
    return context;
  }
}
