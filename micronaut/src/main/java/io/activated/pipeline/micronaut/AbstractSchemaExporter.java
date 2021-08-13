package io.activated.pipeline.micronaut;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import io.micronaut.runtime.Micronaut;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public abstract class AbstractSchemaExporter {

  public void export(Class applicationClass, String filePath) throws IOException {

    var app = Micronaut.build(new String[0]).classes(applicationClass).build().start();
    var schema = app.getBean(GraphQLSchema.class);
    var printer = new SchemaPrinter();
    var out = printer.print(schema);

    BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

    writer.write(out);
    writer.close();

    app.stop();
  }
}
