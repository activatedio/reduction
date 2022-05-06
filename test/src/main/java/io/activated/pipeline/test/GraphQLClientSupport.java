package io.activated.pipeline.test;

import com.google.common.collect.Maps;
import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.client.*;
import com.netflix.graphql.dgs.client.codegen.BaseProjectionNode;
import com.netflix.graphql.dgs.client.codegen.GraphQLQuery;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GraphQLClientSupport {

  private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLClientSupport.class);

  private final SecureRandom secureRandom = new SecureRandom();
  private final Base64.Encoder encoder = Base64.getEncoder().withoutPadding();
  private final WebClient webClient;
  private final WebClientGraphQLClient client;

  private String sessionId;
  private String accessToken;

  public GraphQLClientSupport(GraphQLConfig config) {
    this.webClient = WebClient.create(config.getURL());
    this.client =
        MonoGraphQLClient.createWithWebClient(
            webClient,
            headers -> {
              if (sessionId != null) {
                headers.put("pipeline-session-id", List.of(sessionId));
              }
              if (accessToken != null) {
                headers.put("Authorization", List.of("Bearer " + accessToken));
              }
            });
    newSession();
  }

  public void newSession() {

    var bytes = new byte[32];
    secureRandom.nextBytes(bytes);

    sessionId = encoder.encodeToString(bytes);

    LOGGER.debug("Setting new session id: {}", sessionId);
  }

  /** @return current session id, generating if not yet created */
  public String getSessionIdCreateIfNeeded() {
    if (sessionId == null) {
      newSession();
    }
    return sessionId;
  }

  /**
   * Set session id to use
   *
   * @param sessionId
   */
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public <T> Mono<T> query(
      GraphQLQuery query, BaseProjectionNode projectionNode, String path, TypeRef<T> typeRef) {

    var request = new GraphQLQueryRequest(query, projectionNode);
    return query(request.serialize(), path, typeRef);
  }

  private <T> Mono<T> query(String query, String path, TypeRef<T> typeRef) {

    return query(query)
        .doOnNext(
            _v -> {
              LOGGER.debug(
                  "Sending query to path: {}, query:\n {}\n\n, response:\n{}",
                  path,
                  query,
                  _v.getJson());
            })
        .map(
            _v -> {
              if (_v.hasErrors()) {
                throw new GraphQLErrorException(_v.getErrors().get(0).getMessage());
              }

              return _v.extractValueAsObject(path, typeRef);
            });
  }

  private <T> Mono<GraphQLResponse> query(String query) {

    return client.reactiveExecuteQuery(query, Maps.newHashMap());
  }
}
