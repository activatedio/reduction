package io.activated.pipeline.internal;

import io.activated.pipeline.Reducer;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

public class ReducerKey<S, A> implements Serializable {

  private static final long serialVersionUID = -5124574953061278777L;
  private final Class<S> stateType;
  private final Class<A> actionType;

  private ReducerKey(final Class<S> stateType, final Class<A> actionType) {
    this.stateType = stateType;
    this.actionType = actionType;
  }

  public Class<S> getStateType() {
    return stateType;
  }

  public Class<A> getActionType() {
    return actionType;
  }

  public static <S, A> ReducerKey<S, A> create(
      final Class<S> stateType, final Class<A> actionType) {
    return new ReducerKey(stateType, actionType);
  }

  public static <S, A> ReducerKey<S, A> fromReducerClass(final Class<?> input) {

    for (final var iface : input.getGenericInterfaces()) {
      if (iface instanceof ParameterizedType) {
        var pType = (ParameterizedType) iface;
        if (pType.getRawType() == Reducer.class) {
          var args = pType.getActualTypeArguments();
          return ReducerKey.create((Class<S>) args[0], (Class<A>) args[1]);
        }
      }
    }
    return null;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final ReducerKey that = (ReducerKey) o;
    return Objects.equals(stateType, that.stateType) && Objects.equals(actionType, that.actionType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stateType, actionType);
  }

  @Override
  public String toString() {
    return "ReducerKey{" + "stateType=" + stateType + ", actionType=" + actionType + '}';
  }
}
