package io.activated.pipeline.micronaut.e2e;

import com.jayway.jsonpath.TypeRef;
import io.activated.pipeline.micronaut.e2e.client.ExternalStateProjectionRoot;
import io.activated.pipeline.micronaut.e2e.types.ExternalState;
import io.activated.pipeline.test.GraphQLClientSupport;
import io.activated.pipeline.test.StateDriver;

public class ExternalStateDriver extends StateDriver<ExternalState> {

  protected ExternalStateDriver(GraphQLClientSupport support) {
    super(support, new TypeRef<>() {}, new ExternalStateProjectionRoot().state().keys());
  }
}
