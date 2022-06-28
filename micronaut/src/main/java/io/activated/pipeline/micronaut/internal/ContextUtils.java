package io.activated.pipeline.micronaut.internal;

import io.activated.pipeline.*;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.context.ServerRequestContext;
import java.util.List;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextUtils {

  public static class Result {
    private final HttpRequest<?> request;
    private final Context context;

    public Result(HttpRequest<?> request, Context context) {
      this.request = request;
      this.context = context;
    }

    public HttpRequest<?> getRequest() {
      return request;
    }

    public Context getContext() {
      return context;
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ContextUtils.class);

  public static Result create() {

    var request = ServerRequestContext.currentRequest().get();
    var context = new Context();
    var headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
    // TODO - How to test both types of headers
    headers.putAll(request.getHeaders().asMap());
    context.setHeaders(headers);
    context.setAttributes(request.getAttributes().asMap());
    LOGGER.info("using context: " + context);
    return new Result(request, context);
  }
}
