package io.activated.pipeline.internal;

import io.activated.pipeline.InitialState;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

public class InitialStateKey<S> implements Serializable {

  private static final long serialVersionUID = -5124574953061278777L;
  private final Class<S> stateType;

  private InitialStateKey(final Class<S> stateType) {
    this.stateType = stateType;
  }

  public static <S> InitialStateKey<S> create(final Class<S> stateType) {
    return new InitialStateKey(stateType);
  }

  public static <S> InitialStateKey<S> fromInitialStateClass(final Class<?> input) {

    for (final var iface : input.getGenericInterfaces()) {
      if (iface instanceof ParameterizedType) {
        final var pType = (ParameterizedType) iface;
        if (pType.getRawType() == InitialState.class) {
          final var args = pType.getActualTypeArguments();
          return InitialStateKey.create((Class<S>) args[0]);
        }
      }
    }
    return null;
  }

  public Class<S> getStateType() {
    return stateType;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final InitialStateKey<?> that = (InitialStateKey<?>) o;
    return Objects.equals(stateType, that.stateType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stateType);
  }

  @Override
  public String toString() {
    return "InitialStateKey{" + "stateType=" + stateType + '}';
  }
}
