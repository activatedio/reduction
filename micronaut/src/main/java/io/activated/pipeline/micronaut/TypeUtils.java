package io.activated.pipeline.micronaut;

import io.activated.pipeline.Exportable;
import java.lang.reflect.ParameterizedType;

public final class TypeUtils {

  private TypeUtils() {}

  public static boolean isExportable(Class<?> input) {
    return Exportable.class.isAssignableFrom(input);
  }

  public static <I extends Exportable<E>, E> Class<E> toExportable(Class<?> input) {

    if (!isExportable(input)) {
      throw new IllegalArgumentException("input class is not exportable");
    }

    var genericInterfaces = input.getGenericInterfaces();
    for (var genericInterface : genericInterfaces) {
      if (genericInterface instanceof ParameterizedType) {
        var paramterizedType = ((ParameterizedType) genericInterface);
        if (paramterizedType.getRawType() == Exportable.class) {
          return (Class<E>) paramterizedType.getActualTypeArguments()[0];
        }
      }
    }

    throw new IllegalArgumentException("unexpected error encountered");
  }
}
