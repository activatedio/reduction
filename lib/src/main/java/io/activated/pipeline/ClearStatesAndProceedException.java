package io.activated.pipeline;

/** When thrown the pipeline will clear the state and continue to process the request */
public class ClearStatesAndProceedException extends RuntimeException
    implements ClearAllStates, Ignore {
  public ClearStatesAndProceedException() {}
}
