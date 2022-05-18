package io.activated.pipeline.micronaut.cart;

import io.activated.pipeline.Context;
import io.activated.pipeline.Reducer;
import io.activated.pipeline.annotations.Operation;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Operation
@Singleton
public class PromoCodeReducer implements Reducer<Cart, PromoCodeAction> {
  @Override
  public Mono<Cart> reduce(Context context, Cart state, PromoCodeAction action) {

    return Mono.just(action)
        .map(PromoCodeAction::getPromoCodes)
        .doOnNext(state::setPromoCodes)
        .map(pcs -> state);
  }
}
