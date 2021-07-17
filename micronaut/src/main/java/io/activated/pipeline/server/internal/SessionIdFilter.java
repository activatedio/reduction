package io.activated.pipeline.server.internal;

import java.io.IOException;
import java.net.URI;
import java.util.function.Supplier;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.activated.pipeline.PipelineException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

public class SessionIdFilter extends GenericFilterBean {

  private static final String HEADER_NAME = "pipeline-session-id";

  // private final Supplier<String> sessionIdSupplier = new SessionIdSupplierImpl();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;

    var sessionId = req.getHeader(HEADER_NAME);

    if (sessionId != null) {
      req.setAttribute(Constants.SESSION_ID_ATTRIBUTE_NAME, sessionId);
    }

    chain.doFilter(request, response);
  }

}
