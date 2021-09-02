package io.activated.pipeline;

public final class ClearStateAndProceedException extends IgnoreException implements ClearState {

  public ClearStateAndProceedException(Object returnInstead) {
    super(returnInstead);
  }
}
