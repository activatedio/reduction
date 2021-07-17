package scenarios.no_existing_schema;

import static org.assertj.core.api.Assertions.assertThat;

import com.graphql.spring.boot.test.GraphQLTestTemplate;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import redis.embedded.RedisServer;
import scenarios.model.cart.Address;
import scenarios.model.cart.Cart;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = Application.class)
public class ApplicationTest {

  @Autowired private GraphQLTestTemplate template;

  @BeforeEach
  public void setUp() {}

  /*
  @Test
  public void scenario() throws IOException {

    var resp = template.postForResource("graphql/cart1.graphql");
    assertThat(resp.isOk()).isTrue();

    var c = new Cart();
    var a = new Address();
    a.setCity("Test City");
    c.setShippingAddress(a);
    var got = resp.get("data.cart.state", Cart.class);
    assertThat(got).isEqualTo(c);

    resp = template.postForResource("graphql/setAddress.graphql");
    assertThat(resp.isOk()).isTrue();

    got = resp.get("data.cartSetAddress.state", Cart.class);
    c.getShippingAddress().setStreet("New Street Value 2");
    c.getShippingAddress().setCity("Test City 4");
    assertThat(got).isEqualTo(c);
  }
   */
}
