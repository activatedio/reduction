package scenarios.model.books;

import graphql.kickstart.tools.GraphQLResolver;
import org.springframework.stereotype.Component;

@Component
public class BookResolver
    implements GraphQLResolver<Book> /* This class is a resolver for the Book "Data Class" */ {

  public Author author(Book book) {

    var a = new Author();
    a.setId(123);
    a.setName("Name");

    return a;
  }
}
