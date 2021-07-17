package io.activated.pipeline.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.activated.pipeline.GetResult;
import io.activated.pipeline.Pipeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import scenarios.model.cart.Cart;

@ExtendWith(MockitoExtension.class)
public class GetDataFetcherImplTest {

  private final Class<Cart> stateClass = Cart.class;
  @Mock private Pipeline pipeline;

  private GetDataFetcherImpl<Cart> unit;

  @BeforeEach
  public void setUp() {
    unit = new GetDataFetcherImpl<Cart>(pipeline, Cart.class);
  }

  @Test
  public void get() throws Exception {
    final var result = new GetResult<Cart>();
    when(pipeline.get(Cart.class)).thenReturn(result);
    assertThat(unit.get(null)).isEqualTo(result);
  }
}
