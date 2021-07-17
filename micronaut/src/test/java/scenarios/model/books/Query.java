package scenarios.model.books;

import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.List;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;

@Component
public class Query implements GraphQLQueryResolver {

  public List<Book> books() {
    var b1 = new Book();
    b1.setId(1);
    b1.setName("Name 1");
    var b2 = new Book();
    b2.setId(2);
    b2.setName("Name 2");
    List<Book> books = Lists.newArrayList(b1, b2);
    return books;
  }
}
