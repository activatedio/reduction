package io.activated.pipeline.test;

import com.google.common.collect.Maps;
import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.client.DefaultGraphQLClient;
import com.netflix.graphql.dgs.client.GraphQLClient;
import com.netflix.graphql.dgs.client.GraphQLResponse;
import com.netflix.graphql.dgs.client.HttpResponse;
import com.netflix.graphql.dgs.client.codegen.BaseProjectionNode;
import com.netflix.graphql.dgs.client.codegen.GraphQLQuery;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class GraphQLClientSupport {

  private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLClientSupport.class);

  private final SecureRandom secureRandom = new SecureRandom();
  private final Base64.Encoder encoder = Base64.getEncoder().withoutPadding();

  private final com.netflix.graphql.dgs.client.GraphQLClient client;
  private final RestTemplate restTemplate;

  private String sessionId;
  private String accessToken;

  public GraphQLClientSupport(GraphQLConfig config) {
    this.client = new DefaultGraphQLClient(config.getURL());
    restTemplate = new RestTemplate();
    newSession();
  }

  public void newSession() {

    var bytes = new byte[32];
    secureRandom.nextBytes(bytes);

    sessionId = encoder.encodeToString(bytes);

    LOGGER.debug("Setting new session id: {}", sessionId);
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public <T> void query(
      GraphQLQuery query,
      BaseProjectionNode projectionNode,
      String path,
      TypeRef<T> typeRef,
      Consumer<T> success,
      Consumer<GraphQLErrorException> fail) {

    var request = new GraphQLQueryRequest(query, projectionNode);

    try {
      var resp = query(request.serialize(), path, typeRef);
      success.accept(resp);
    } catch (GraphQLErrorException e) {
      fail.accept(e);
    }
  }

  private <T> T query(String query, String path, TypeRef<T> typeRef) {

    var value = query(query);

    LOGGER.debug("Sending query to path: {}, query:\n {}\n\n, response:\n{}", path, query, value.getJson());

    // We only return the first error here.  May need to change
    if (value.hasErrors()) {
      throw new GraphQLErrorException(value.getErrors().get(0).getMessage());
    }

    return value.extractValueAsObject(path, typeRef);
  }

  private <T> GraphQLResponse query(String query) {

    var result =
        client.executeQuery(
            query,
            Maps.newHashMap(),
            (url, headers, body) -> {
              HttpHeaders requestHeaders = new HttpHeaders();
              headers.forEach(requestHeaders::put);
              if (sessionId != null) {
                requestHeaders.add("pipeline-session-id", sessionId);
              }
              if (accessToken != null) {
                requestHeaders.add("Authorization", "Bearer " + accessToken);
              }
              LOGGER.debug("Request headers: {}", requestHeaders);
              ResponseEntity<String> exchange =
                  restTemplate.exchange(
                      url, HttpMethod.POST, new HttpEntity(body, requestHeaders), String.class);
              return new HttpResponse(exchange.getStatusCodeValue(), exchange.getBody());
            });
    return result;
  }
}
