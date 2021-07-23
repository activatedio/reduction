package io.activated.pipeline.micronaut.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Maps;
import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.client.DefaultGraphQLClient;
import com.netflix.graphql.dgs.client.GraphQLClient;
import com.netflix.graphql.dgs.client.GraphQLResponse;
import com.netflix.graphql.dgs.client.HttpResponse;
import io.activated.pipeline.GetResult;
import io.activated.pipeline.micronaut.internal.NewSessionIdSupplier;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@MicronautTest
class MicronautE2eTest {

  @Inject private EmbeddedServer server;

  private GraphQLClient client;
  private RestTemplate restTemplate = new RestTemplate();

  private TypeRef<GetResult<Cart>> cartTypeRef = new TypeRef<GetResult<Cart>>() {};

  private String sessionId;

  private final String CART_QUERY =
      "{ cart { state {billingAddress{name, street, city, state, zip}, shippingAddress{name, street, city, state, zip}}}}";

  private final String CART_SET_ADDRESS_MUTATION =
      "mutation { cartSetAddress(action: {addressType: \"B\", address: {city: \"Test City 2\"}}) { state {billingAddress{name, street, city, state, zip}, shippingAddress{name, street, city, state, zip}}}}";

  @BeforeEach
  void setUp() {

    client =
        new DefaultGraphQLClient(
            String.format("http://%s:%d/graphql", server.getHost(), server.getPort()));
    sessionId = new NewSessionIdSupplier().get();
  }

  @Test
  void noSessionId() {

    var result = query(CART_QUERY, null);

    assertThat(result.getErrors()).hasSize(1);
    assertThat(result.getErrors().get(0).getMessage())
        .contains("Could not obtain key from session");
  }

  @Test
  void secnario() {

    var cart = query(CART_QUERY, sessionId, "cart", cartTypeRef);

    var reference = new Cart();

    var shippingAddress = new Address();
    shippingAddress.setCity("Test City");
    reference.setShippingAddress(shippingAddress);

    assertThat(cart.getState()).isEqualTo(reference);

    cart = query(CART_SET_ADDRESS_MUTATION, sessionId, "cartSetAddress", cartTypeRef);

    var billingAddress = new Address();
    billingAddress.setCity("Test City 2");
    reference.setBillingAddress(billingAddress);

    assertThat(cart.getState()).isEqualTo(reference);
  }

  private <T> T query(String query, String sessionId, String path, TypeRef<T> typeRef) {
    return query(query, sessionId).extractValueAsObject(path, typeRef);
  }

  private <T> GraphQLResponse query(String query, String sessionId) {

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
              ResponseEntity<String> exchange =
                  restTemplate.exchange(
                      url, HttpMethod.POST, new HttpEntity(body, requestHeaders), String.class);
              return new HttpResponse(exchange.getStatusCodeValue(), exchange.getBody());
            });
    return result;
  }
}
