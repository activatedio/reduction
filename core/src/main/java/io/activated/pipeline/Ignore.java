package io.activated.pipeline;

/** Instructs pipeline to ignore the exception and return the provided value */
public interface Ignore {

  <S> S returnInstead();
}
