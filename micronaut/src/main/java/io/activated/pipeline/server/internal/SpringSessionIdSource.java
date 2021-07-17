package io.activated.pipeline.server.internal;

import io.activated.pipeline.env.SessionIdSupplier;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpringSessionIdSource implements SessionIdSupplier {

  // Spring knows how to scope this bean properly
  private final HttpServletRequest request;

  @Autowired
  public SpringSessionIdSource(final HttpServletRequest request) {
    this.request = request;
  }

  @Override
  public String get() {
    return (String) request.getAttribute(Constants.SESSION_ID_ATTRIBUTE_NAME);
  }
}
