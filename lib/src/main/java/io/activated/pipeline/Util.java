package io.activated.pipeline;

public final class Util {

  private Util() {};

  public static boolean isEmpty(String input) {
    return (input == null || input.trim().isEmpty());
  }
}
