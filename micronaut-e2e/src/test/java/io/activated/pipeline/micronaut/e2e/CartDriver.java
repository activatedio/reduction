package io.activated.pipeline.micronaut.e2e;

import com.jayway.jsonpath.TypeRef;
import io.activated.pipeline.micronaut.e2e.client.CartProjectionRoot;
import io.activated.pipeline.micronaut.e2e.types.Cart;
import io.activated.pipeline.test.GraphQLClientSupport;
import io.activated.pipeline.test.StateDriver;

public class CartDriver extends StateDriver<Cart> {

  protected CartDriver(GraphQLClientSupport support) {
    super(
        support,
        new TypeRef<>() {},
        new CartProjectionRoot()
            .state()
            .promoCodes()
            .stringArrayValue()
            .threadName()
            .pipelineSessionId()
            .longValue()
            .shippingAddress()
            .state());
  }
}
