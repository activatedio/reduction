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

  private String accessToken;

  private List<String> cookies;

  public GraphQLClientSupport(GraphQLConfig config) {
    this.webClient = WebClient.builder().baseUrl(config.getURL()).build();
    this.client =
        MonoGraphQLClient.createWithWebClient(
            webClient,
            headers -> {
              if (accessToken != null) {
                headers.put("Authorization", List.of("Bearer " + accessToken));
              }
              if (cookies != null) {
                headers.put("Cookie", cookies);
              }
            });
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

              if (_v.getHeaders().containsKey("set-cookie")) {
                cookies = _v.getHeaders().get("set-cookie");
              }

              return _v.extractValueAsObject(path, typeRef);
            });
  }

  private <T> Mono<GraphQLResponse> query(String query) {

    return client.reactiveExecuteQuery(query, Maps.newHashMap());
  }
}
