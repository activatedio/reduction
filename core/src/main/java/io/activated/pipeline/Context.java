package io.activated.pipeline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Context {

  private Map<String, List<String>> headers = new HashMap<>();
  private Map<String, Object> attributes = new HashMap<>();

  private PrincipalWrapper principalWrapper;

  public Map<String, List<String>> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, List<String>> headers) {
    this.headers = headers;
  }

  public Map<String, Object> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  public PrincipalWrapper getPrincipalWrapper() {
    return principalWrapper;
  }

  public void setPrincipalWrapper(PrincipalWrapper principalWrapper) {
    this.principalWrapper = principalWrapper;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Context context = (Context) o;
    return Objects.equals(headers, context.headers)
        && Objects.equals(attributes, context.attributes)
        && Objects.equals(principalWrapper, context.principalWrapper);
  }

  @Override
  public int hashCode() {
    return Objects.hash(headers, attributes, principalWrapper);
  }

  @Override
  public String toString() {
    return "Context{"
        + "headers="
        + headers
        + ", attributes="
        + attributes
        + ", principalWrapper="
        + principalWrapper
        + '}';
  }
}
