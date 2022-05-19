package io.activated.pipeline.micronaut.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import io.activated.pipeline.micronaut.e2e.client.*;
import io.activated.pipeline.micronaut.e2e.types.*;
import io.activated.pipeline.test.GraphQLClientSupport;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ExternalStateTest {

  @Test
  void scenario() {

    var driver = new ExternalStateDriver(new GraphQLClientSupport(MicronautRuntime.CONFIG));

    // Set shipping address

    var query =
        new ExternalStatePutEntryActionGraphQLQuery.Builder()
            .action(PutEntryActionInput.newBuilder().key("key1").value("value1").build())
            .build();

    driver.query(query, "externalStatePutEntryAction");

    assertThat(driver.getLastGraphQLError()).isNull();
    assertThat(driver.getLastState().getKeys()).isEqualTo(List.of("key1"));
  }
}
