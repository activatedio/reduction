package io.activated.pipeline.server;

import java.util.List;
import java.util.Objects;

public class GraphQLResult<T> {

  private T data;
  private List<GraphQLError> errors;

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public List<GraphQLError> getErrors() {
    return errors;
  }

  public void setErrors(List<GraphQLError> errors) {
    this.errors = errors;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GraphQLResult<?> that = (GraphQLResult<?>) o;
    return Objects.equals(data, that.data) && Objects.equals(errors, that.errors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, errors);
  }

  @Override
  public String toString() {
    return "GraphQLResult{" + "data=" + data + ", errors=" + errors + '}';
  }
}
