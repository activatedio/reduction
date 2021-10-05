package io.activated.pipeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BlockingReducerTest {

    private static class State {
        private String to;
    }

    private static class Action {
        private String from;
    }

    private static class TestableBlockingReducer implements BlockingReducer<State, Action> {

        @Override
        public void blockingReduce(State state, Action action) {
            state.to = action.from;
        }
    }

    private static final String payload = "test-payload";

    private TestableBlockingReducer unit;

    @BeforeEach
    public void setUp() {
        unit = new TestableBlockingReducer();
    }

    @Test
    public void reduce() {

        var state = new State();
        var action = new Action();

        action.from = payload;

        var got = FlowableScan.fromPublisher(unit.reduce(state, action)).blockingSingle();

        assertThat(got).isSameAs(state);
        assertThat(got.to).isSameAs(payload);
    }

}