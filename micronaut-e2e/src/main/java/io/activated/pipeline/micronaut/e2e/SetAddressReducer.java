package io.activated.pipeline.micronaut.e2e;

import io.activated.pipeline.PipelineException;
import io.activated.pipeline.Reducer;
import io.activated.pipeline.annotations.Operation;

import javax.inject.Singleton;

@Operation
@Singleton
public class SetAddressReducer implements Reducer<Cart, SetAddress> {

  @Override
  public void reduce(Cart state, SetAddress action) {

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
