package io.activated.pipeline.server.internal;

import io.activated.pipeline.PipelineException;
import io.activated.pipeline.env.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringObjectProvider implements ObjectProvider {

  private final ApplicationContext applicationContext;

  @Autowired
  public SpringObjectProvider(final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public <T> T get(final Class<T> type) {
    var bean = applicationContext.getBean(type);
    if (bean == null) {
      throw new PipelineException("Bean not found for class: " + type);
    }
    return bean;
  }
}
