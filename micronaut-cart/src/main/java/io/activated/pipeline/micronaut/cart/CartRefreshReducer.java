package io.activated.pipeline.micronaut.cart;

import io.activated.pipeline.Reducer;
import io.activated.pipeline.RefreshAction;
import io.activated.pipeline.annotations.Operation;
import javax.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Operation
@Singleton
public class CartRefreshReducer implements Reducer<Cart, RefreshAction> {

  @Override
  public Publisher<Cart> reduce(Cart state, RefreshAction action) {

    state.incrementCount();
    return Mono.just(state);
  }
}
