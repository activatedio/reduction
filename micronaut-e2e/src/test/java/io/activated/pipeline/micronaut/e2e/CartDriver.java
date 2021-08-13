package io.activated.pipeline.micronaut.e2e;

import com.netflix.graphql.dgs.client.codegen.BaseProjectionNode;
import io.activated.pipeline.micronaut.cart.client.CartProjectionRoot;
import io.activated.pipeline.micronaut.cart.client.Cart_StateProjection;
import io.activated.pipeline.micronaut.cart.types.Cart;
import io.activated.pipeline.test.GraphQLClientSupport;
import io.activated.pipeline.test.StateDriver;

public class CartDriver extends StateDriver<Cart> {
  protected CartDriver(GraphQLClientSupport client) {
    super(client, new CartProjectionRoot().state().billingAddress().state());
  }
}