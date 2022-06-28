package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.Context;
import io.activated.pipeline.micronaut.ContextBuilder;
import io.activated.pipeline.micronaut.ContextFactory;
import jakarta.inject.Singleton;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import reactor.core.publisher.Mono;

@Singleton
public class ContextFactoryImpl implements ContextFactory {

  private final List<ContextBuilder> contextBuilders;

  public ContextFactoryImpl(List<ContextBuilder> contextBuilders) {
    this.contextBuilders =
        contextBuilders.stream()
            .sorted(Comparator.comparingInt(ContextBuilder::order))
            .collect(Collectors.toList());
  }

  @Override
  public Mono<Context> create() {

    var initial = initial();

    var req = initial.getRequest();
    var ctx = initial.getContext();

    var m = Mono.just(ctx);

    for (var cb : contextBuilders) {
      m = m.flatMap(_ctx -> cb.build(req, _ctx));
    }

    return m;
  }

  protected ContextUtils.Result initial() {
    return ContextUtils.create();
  }
}
