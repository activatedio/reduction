package io.activated.pipeline.server;

import static org.assertj.core.api.Assertions.assertThat;

import io.activated.pipeline.Pipeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import scenarios.model.cart.Cart;
import scenarios.model.cart.SetAddressReducer;

@ExtendWith(MockitoExtension.class)
class DataFetcherFactoryImplTest {

  private final Class<Cart> stateClass = Cart.class;
  private final Class<SetAddressReducer> actionClass = SetAddressReducer.class;

  @Mock private Pipeline pipeline;

  private DataFetcherFactoryImpl unit;

  @BeforeEach
  public void setUp() {
    unit = new DataFetcherFactoryImpl(pipeline);
  }

  @Test
  public void getGetDataFetcher() {
    var got = unit.getGetDataFetcher(stateClass);
    assertThat(got).isInstanceOf(GetDataFetcherImpl.class);
  }

  @Test
  public void getSetDataFetcher() {
    var got = unit.getSetDataFetcher(stateClass, actionClass);
    assertThat(got).isInstanceOf(SetDataFetcherImpl.class);
  }
}
