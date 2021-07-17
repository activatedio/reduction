package io.activated.pipeline;

import io.activated.base.JUnit5ModelTestSupport;
import io.activated.pipeline.fixtures.DummyState;

public class SetResultTest extends JUnit5ModelTestSupport<SetResult<DummyState>> {

  @Override
  protected SetResult<DummyState> makeReference() {

    var state = new DummyState();
    state.setValue("1");

    var result = new SetResult<DummyState>();
    result.setState(state);

    return result;
  }

  @Override
  protected SetResult<DummyState> modifyReference(SetResult<DummyState> input) {

    input.getState().setValue("2");
    return input;
  }
}
