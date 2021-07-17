package scenarios.existing_schema;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Lists;
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
import scenarios.model.books.Book;
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
  public void scenario_cart() throws IOException {

    var resp = template.postForResource("graphql/cart1.graphql");
    assertThat(resp.isOk()).isTrue();

    var c = new Cart();
    var a = new Address();
    a.setCity("Test City");
    c.setShippingAddress(a);
    var got = resp.get("data.cart.state", Cart.class);
    assertThat(got).isEqualTo(c);

    // TODO - More tests needed here
  }

  @Test
  public void scenario_books() throws IOException {

    var resp = template.postForResource("graphql/books.graphql");
    assertThat(resp.isOk()).isTrue();

    var b1 = new Book();
    b1.setId(1);
    b1.setName("Name 1");
    var b2 = new Book();
    b2.setId(2);
    b2.setName("Name 2");
    var got = resp.getList("data.books", Book.class);
    assertThat(got).isEqualTo(Lists.newArrayList(b1, b2));

    // TODO - More tests needed here
  }
   */
}
