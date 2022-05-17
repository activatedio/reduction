package io.activated.pipeline.micronaut;

import io.activated.pipeline.*;
import io.micronaut.http.context.ServerRequestContext;
import java.util.List;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContextUtils.class);

  public static Context getContext() {

    var request = ServerRequestContext.currentRequest().get();
    var context = new Context();
    var headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
    // TODO - How to test both types of headers
    headers.putAll(request.getHeaders().asMap());
    context.setHeaders(headers);
    context.setAttributes(request.getAttributes().asMap());
    LOGGER.info("using context: " + context);
    return context;
  }
}
