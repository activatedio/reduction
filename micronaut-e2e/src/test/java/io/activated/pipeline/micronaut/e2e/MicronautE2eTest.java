package io.activated.pipeline.micronaut.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Maps;
import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.client.DefaultGraphQLClient;
import com.netflix.graphql.dgs.client.GraphQLClient;
import com.netflix.graphql.dgs.client.GraphQLResponse;
import com.netflix.graphql.dgs.client.HttpResponse;
import io.activated.pipeline.micronaut.cart.Application;
import io.activated.pipeline.micronaut.internal.NewSessionIdSupplier;
import io.activated.pipeline.test.GraphQLClientSupport;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.server.EmbeddedServer;
import javax.inject.Inject;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class CartTest {

  private static ApplicationContext APPLICATION_CONTEXT;
  private static GraphQLClient GRAPHQL_CLIENT;

  @BeforeAll
  public static void setUpAll() {
    APPLICATION_CONTEXT = Micronaut.build(new String[0]).classes(Application.class).build();
    var server = APPLICATION_CONTEXT.getBean(EmbeddedServer.class);
    GRAPHQL_CLIENT = new DefaultGraphQLClient(String.format("%s://%s:%d", server.getScheme(),
        server.getHost(), server.getPort()));
  }

  @AfterAll
  public static void tearDownAll() {
    APPLICATION_CONTEXT.stop();
  }

  @Test
  void secnario() {

    var driver = new CartDriver(new GraphQLClientSupport(GRAPHQL_CLIENT));

  }

}
