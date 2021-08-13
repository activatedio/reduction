package io.activated.pipeline.micronaut.cart;

import io.activated.pipeline.micronaut.AbstractSchemaExporter;
import java.io.IOException;

public class SchemaExporter extends AbstractSchemaExporter {

  public static void main(String[] args) throws IOException {

    var exporter = new SchemaExporter();
    exporter.export(SchemaExporter.class, args[0]);
  }
}
