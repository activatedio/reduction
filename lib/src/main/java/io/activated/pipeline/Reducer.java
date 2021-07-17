package io.activated.pipeline;

public interface Reducer<S, A> {
  void reduce(S state, A action);
}
