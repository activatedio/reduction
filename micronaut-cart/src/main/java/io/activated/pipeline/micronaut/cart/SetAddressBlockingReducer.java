package io.activated.pipeline.micronaut.cart;

import io.activated.pipeline.BlockingReducer;
import io.activated.pipeline.PipelineException;
import io.activated.pipeline.annotations.Operation;
import jakarta.inject.Singleton;

@Operation
@Singleton
public class SetAddressBlockingReducer implements BlockingReducer<Cart, SetAddress> {

  @Override
  public void blockingReduce(Cart state, SetAddress action) {

    switch (action.getAddressType()) {
      case "B":
        state.setBillingAddress(action.getAddress());
        break;
      case "S":
        state.setShippingAddress(action.getAddress());
        break;
      default:
        throw new PipelineException("Invalid address type");
    }
  }
}
