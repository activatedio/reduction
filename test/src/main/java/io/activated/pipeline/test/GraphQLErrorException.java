package io.activated.pipeline.test;

public class GraphQLErrorException extends RuntimeException {

  public GraphQLErrorException() {
    super();
  }

  public GraphQLErrorException(String message) {
    super(message);
  }

  public GraphQLErrorException(String message, Throwable cause) {
    super(message, cause);
  }

  public GraphQLErrorException(Throwable cause) {
    super(cause);
  }

  protected GraphQLErrorException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
