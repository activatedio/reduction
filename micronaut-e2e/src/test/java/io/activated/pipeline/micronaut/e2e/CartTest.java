package io.activated.pipeline.micronaut.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import com.netflix.graphql.dgs.client.DefaultGraphQLClient;
import com.netflix.graphql.dgs.client.GraphQLClient;
import io.activated.pipeline.micronaut.cart.Application;
import io.activated.pipeline.micronaut.cart.client.CartSetAddressGraphQLQuery;
import io.activated.pipeline.micronaut.cart.types.AddressInput;
import io.activated.pipeline.micronaut.cart.types.SetAddressInput;
import io.activated.pipeline.test.GraphQLClientSupport;
import io.activated.pipeline.test.GraphQLConfig;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.server.EmbeddedServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CartTest {

  private static ApplicationContext APPLICATION_CONTEXT;
  private static GraphQLConfig CONFIG;

  @BeforeAll
  public static void setUpAll() {
    APPLICATION_CONTEXT = Micronaut.run(Application.class);
    var server = APPLICATION_CONTEXT.getBean(EmbeddedServer.class);
    CONFIG = new GraphQLConfig() {
      @Override
      public String getURL() {
        return String.format("%s://%s:%d/graphql", server.getScheme(), server.getHost(), server.getPort());
      }
    };
  }

  @AfterAll
  public static void tearDownAll() {
    APPLICATION_CONTEXT.stop();
  }

  @Test
  void scenario() {

    var driver = new CartDriver(new GraphQLClientSupport(CONFIG));

    // Set shipping address

    var query = new CartSetAddressGraphQLQuery.Builder().action(SetAddressInput.newBuilder()
        .addressType("S").address(AddressInput.newBuilder()
            .state("WA")
            .build())
        .build()).build();

    driver.query(query, "cartSetAddress");

    assertThat(driver.getLastGraphQLError()).isNull();
    assertThat(driver.getLastState().getShippingAddress().getState()).isEqualTo("WA");

  }

}
