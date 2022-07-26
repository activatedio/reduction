package io.activated.pipeline.micronaut.e2e;

import io.activated.pipeline.micronaut.cart.Application;
import io.activated.pipeline.test.GraphQLConfig;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.server.EmbeddedServer;

public class MicronautRuntime {

  public static ApplicationContext APPLICATION_CONTEXT;
  public static GraphQLConfig CONFIG;

  static {
    APPLICATION_CONTEXT = Micronaut.build(new String[] {}).classes(Application.class).start();

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
}
