package io.activated.pipeline.builtin.security;

import io.activated.pipeline.PipelineException;

public class SecurityException extends PipelineException {
  public SecurityException(String message) {
    super(message);
  }
}
