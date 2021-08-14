package io.activated.pipeline.test;

public class GetResult<S> {

  private S state;

  public S getState() {
    return state;
  }

  public void setState(S state) {
    this.state = state;
  }
}
