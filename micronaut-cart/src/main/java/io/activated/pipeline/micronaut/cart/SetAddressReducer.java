package io.activated.pipeline.micronaut.cart;

import io.activated.pipeline.Context;
import io.activated.pipeline.PipelineException;
import io.activated.pipeline.Reducer;
import io.activated.pipeline.annotations.Operation;
import io.micronaut.core.async.publisher.Publishers;
import javax.inject.Singleton;
import org.reactivestreams.Publisher;

@Operation
@Singleton
public class SetAddressReducer implements Reducer<Cart, SetAddress> {

  @Override
  public Publisher<Cart> reduce(Context context, Cart state, SetAddress action) {

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
    return Publishers.just(state);
  }
}
