package io.activated.pipeline.server.internal;

import io.activated.pipeline.env.PrincipalSupplier;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PrincipalSupplierImpl implements PrincipalSupplier {

  private final HttpServletRequest request;

  public PrincipalSupplierImpl(HttpServletRequest request) {
    this.request = request;
  }

  @Override
  public Principal get() {
    return request.getUserPrincipal();
  }
}
