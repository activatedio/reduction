package io.activated.pipeline.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.activated.pipeline.Mapper;
import io.activated.pipeline.PipelineException;
import java.util.Map;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;

public class PulsarChangeLogger extends AbstractChangeLogger {

  private final Producer<String> producer;

  public PulsarChangeLogger(Producer<String> producer) {
    this.producer = producer;
  }

  @Override
  public void logInternal(Map<String, Object> values) {

    try {
      producer.send(Mapper.OBJECT_MAPPER.writeValueAsString(values));
    } catch (PulsarClientException e) {
      throw new PipelineException(e);
    } catch (JsonProcessingException e) {
      throw new PipelineException(e);
    }
  }
}
