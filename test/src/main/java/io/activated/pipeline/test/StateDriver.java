package io.activated.pipeline.test;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.client.codegen.BaseProjectionNode;
import com.netflix.graphql.dgs.client.codegen.GraphQLQuery;
import java.util.function.Consumer;

public abstract class StateDriver<S> {

  private final TypeRef<GetResult<S>> typeRef;
  private final GraphQLClientSupport client;
  private final BaseProjectionNode projectionNode;

  private final Consumer<GraphQLErrorException> defaultErrorConsumer =
      (e) -> {
        lastGraphQLError = e;
      };

  private final Consumer<GetResult<S>> defaultSuccessConsumer =
      (s) -> {
        lastState = s;
      };

  private GraphQLErrorException lastGraphQLError;
  private GetResult<S> lastState;

  protected StateDriver(GraphQLClientSupport client, TypeRef<GetResult<S>> typeRef, BaseProjectionNode projectionNode) {
    this.client = client;
    this.typeRef = typeRef;
    this.projectionNode = projectionNode;
  }

  public GraphQLErrorException getLastGraphQLError() {
    return lastGraphQLError;
  }

  public S getLastState() {
    return lastState != null ? lastState.getState() : null;
  }

  public void setMutation(GraphQLQuery query, String path) {
    client.query(
        query, projectionNode, path, typeRef, defaultSuccessConsumer, defaultErrorConsumer);
  }

}
