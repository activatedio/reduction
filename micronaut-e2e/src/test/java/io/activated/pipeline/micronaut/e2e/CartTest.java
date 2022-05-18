package io.activated.pipeline.micronaut.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import io.activated.pipeline.micronaut.cart.Application;
import io.activated.pipeline.micronaut.cart.client.*;
import io.activated.pipeline.micronaut.cart.types.*;
import io.activated.pipeline.test.GraphQLClientSupport;
import io.activated.pipeline.test.GraphQLConfig;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.server.EmbeddedServer;
import java.util.List;
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
    CONFIG =
        new GraphQLConfig() {
          @Override
          public String getURL() {
            return String.format(
                "%s://%s:%d/graphql", server.getScheme(), server.getHost(), server.getPort());
          }
        };
  }

  @AfterAll
  public static void tearDownAll() {
    APPLICATION_CONTEXT.stop();
  }

  @Test
  void scenario_invalid() {

    var driver = new CartDriver(new GraphQLClientSupport(CONFIG));

    // Set shipping address

    var query =
        new CartSetAddressGraphQLQuery.Builder()
            .action(
                SetAddressInput.newBuilder()
                    .addressType("SS")
                    .address(AddressInput.newBuilder().state("WA").build())
                    .build())
            .build();

    driver.query(query, "cartSetAddress");

    assertThat(driver.getLastGraphQLError()).isNotNull();
    assertThat(driver.getLastGraphQLError().getMessage())
        .isEqualTo(
            "Exception while fetching data (/cartSetAddress) : Validation failed: addressType: size must be between 0 and 1");
    assertThat(driver.getLastState()).isNull();
  }

  @Test
  void scenario() {

    var driver = new CartDriver(new GraphQLClientSupport(CONFIG));

    // Set shipping address

    var query =
        new CartSetAddressGraphQLQuery.Builder()
            .action(
                SetAddressInput.newBuilder()
                    .addressType("S")
                    .address(AddressInput.newBuilder().state("WA").build())
                    .build())
            .build();

    driver.query(query, "cartSetAddress");

    assertThat(driver.getLastGraphQLError()).isNull();
    assertThat(driver.getLastState().getShippingAddress().getState()).isEqualTo("WA");
    assertThat(driver.getLastState().getLongValue()).isEqualTo(9999l);

    var query2 = new CartGraphQLQuery.Builder().build();

    driver.query(query2, "cart");

    assertThat(driver.getLastGraphQLError()).isNull();
    assertThat(driver.getLastState().getShippingAddress().getState()).isEqualTo("WA");

    // TODO - This causes and error
    var query3 =
        new CartExceptionActionGraphQLQuery.Builder()
            .action(ExceptionActionInput.newBuilder().build())
            .build();

    driver.query(query3, "cartExceptionAction");

    assertThat(driver.getLastGraphQLError()).isNotNull();

    var diagQuery =
        CartDiagnosticActionGraphQLQuery.newRequest()
            .action(DiagnosticActionInput.newBuilder().dummy("test").build())
            .build();

    driver.query(diagQuery, "cartDiagnosticAction");

    assertThat(driver.getLastGraphQLError()).isNotNull();

    assertThat(driver.getLastState().getThreadName()).contains("parallel");
    assertThat(driver.getLastState().getPipelineSessionId()).isNotEmpty();

    var promoCodes = List.of("p1", "p2");

    // Test out the list string
    var promoCodeQuery =
        CartPromoCodeActionGraphQLQuery.newRequest()
            .action(PromoCodeActionInput.newBuilder().promoCodes(promoCodes).build())
            .build();

    driver.query(promoCodeQuery, "cartPromoCodeAction");

    assertThat(driver.getLastGraphQLError()).isNotNull();

    assertThat(driver.getLastState().getPromoCodes()).isEqualTo(promoCodes);
  }
}
