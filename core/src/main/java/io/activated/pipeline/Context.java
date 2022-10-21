package io.activated.pipeline;

import java.util.*;

public class Context {

  private final Map<String, List<String>> headers =
      new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
  private final Map<String, Object> attributes = new HashMap<>();

  private PrincipalWrapper principalWrapper;

  public Map<String, List<String>> getHeaders() {
    return headers;
  }

  public Map<String, Object> getAttributes() {
    return attributes;
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
