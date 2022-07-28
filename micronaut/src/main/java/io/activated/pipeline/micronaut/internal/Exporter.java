package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.Exportable;
import java.util.function.Function;

public class Exporter<S extends Exportable<E>, E> implements Function<S, E> {

  @Override
  public E apply(S s) {
    return s.export();
  }
}
