package io.activated.pipeline.micronaut.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import io.activated.pipeline.micronaut.e2e.client.*;
import io.activated.pipeline.micronaut.e2e.types.*;
import io.activated.pipeline.test.GraphQLClientSupport;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CartTest {
  @Test
  void scenario_invalid() {

    var driver = new CartDriver(new GraphQLClientSupport(MicronautRuntime.CONFIG));

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

    var driver = new CartDriver(new GraphQLClientSupport(MicronautRuntime.CONFIG));

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
    assertThat(driver.getLastState().getStringArrayValue()).containsOnly("a", "b");

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
