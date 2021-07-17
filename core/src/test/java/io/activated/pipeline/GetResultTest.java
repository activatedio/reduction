package io.activated.pipeline;

import io.activated.base.JUnit5ModelTestSupport;
import io.activated.pipeline.fixtures.DummyState;

public class GetResultTest extends JUnit5ModelTestSupport<GetResult<DummyState>> {

  @Override
  protected GetResult<DummyState> makeReference() {

    var state = new DummyState();
    state.setValue("1");

    var result = new GetResult<DummyState>();
    result.setState(state);

    return result;
  }

  @Override
  protected GetResult<DummyState> modifyReference(GetResult<DummyState> input) {

    input.getState().setValue("2");
    return input;
  }
}
