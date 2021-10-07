package io.activated.pipeline;

/** When thrown the pipeline will clear the state and continue to process the request */
public final class ClearStatesAndProceedException extends IgnoreException
    implements ClearAllStates {

  public ClearStatesAndProceedException(Object returnInstead) {
    super(returnInstead);
  }
}
