package io.activated.pipeline.micronaut;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.activated.pipeline.Pipeline;
import io.activated.pipeline.SetResult;
import io.reactivex.*;
import io.reactivex.subjects.PublishSubject;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SetDataFetcherImpl<S, A> implements DataFetcher<Publisher<SetResult<S>>> {

  private final ObjectMapper mapper = new ObjectMapper();

  private final Pipeline pipeline;
  private final Class<S> stateClass;
  private final Class<A> actionClass;

  @Inject
  public SetDataFetcherImpl(
      final Pipeline pipeline, final Class<S> stateClass, final Class<A> actionClass) {
    this.pipeline = pipeline;
    this.stateClass = stateClass;
    this.actionClass = actionClass;
  }

  @Override
  public Publisher<SetResult<S>> get(final DataFetchingEnvironment environment) throws Exception {
    final var arg = environment.getArgument("action");
    final var action = mapper.convertValue(arg, actionClass);
    var pub = pipeline.set(stateClass, action);
    return pub;
  }
}
