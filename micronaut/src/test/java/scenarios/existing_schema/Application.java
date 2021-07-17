package scenarios.existing_schema;

import graphql.kickstart.tools.SchemaParser;
import graphql.schema.GraphQLSchema;
import java.io.FileNotFoundException;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import scenarios.model.books.BookResolver;
import scenarios.model.books.Query;

@SpringBootApplication(
    scanBasePackages = {
      "io.activated.pipeline.server",
      "scenarios.model.books",
      "scenarios.model.cart"
    })
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  @Autowired
  Supplier<GraphQLSchema> schema(Query query, BookResolver bookResolver)
      throws FileNotFoundException {

    return () ->
        SchemaParser.newParser()
            .file("graphql/books.graphqls")
            .resolvers(query, bookResolver)
            .build()
            .makeExecutableSchema();
  }
}
