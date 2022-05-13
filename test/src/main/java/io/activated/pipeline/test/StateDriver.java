package io.activated.pipeline.test;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.client.codegen.BaseProjectionNode;
import com.netflix.graphql.dgs.client.codegen.GraphQLQuery;
import java.util.function.Consumer;

public abstract class StateDriver<S> {

  private final TypeRef<GetResult<S>> typeRef;
  private final GraphQLClientSupport client;
  private final BaseProjectionNode projectionNode;

  private final Consumer<Throwable> defaultErrorConsumer =
      (t) -> {
        lastGraphQLError = t;
      };

  private final Consumer<GetResult<S>> defaultSuccessConsumer =
      (s) -> {
        lastState = s;
      };

  private Throwable lastGraphQLError;
  private GetResult<S> lastState;

  protected StateDriver(
      GraphQLClientSupport client,
      TypeRef<GetResult<S>> typeRef,
      BaseProjectionNode projectionNode) {
    this.client = client;
    this.typeRef = typeRef;
    this.projectionNode = projectionNode;
  }

  public Throwable getLastGraphQLError() {
    return lastGraphQLError;
  }

  public S getLastState() {
    return lastState != null ? lastState.getState() : null;
  }

  public void query(GraphQLQuery query, String path) {
    try {
      client
          .query(query, projectionNode, path, typeRef)
          .doOnNext(defaultSuccessConsumer)
          .doOnError(defaultErrorConsumer)
          .block();
    } catch (Exception e) {
      // we have the error handler above so we can ignore this
    }
  }
}
