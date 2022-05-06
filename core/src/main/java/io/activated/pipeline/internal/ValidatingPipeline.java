package io.activated.pipeline.internal;

import io.activated.pipeline.*;
import java.util.stream.Collectors;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import reactor.core.publisher.Mono;

public class ValidatingPipeline implements Pipeline {

  private final Validator validator;
  private final Pipeline delegate;

  public ValidatingPipeline(ValidatorFactory validatorFactory, Pipeline delegate) {
    this.validator = validatorFactory.getValidator();
    this.delegate = delegate;
  }

  @Override
  public <S> Mono<GetResult<S>> get(Context context, Class<S> stateType) {
    return delegate.get(context, stateType);
  }

  @Override
  public <S, A> Mono<SetResult<S>> set(Context context, Class<S> stateType, A action) {
    return Mono.fromCallable(
            () -> {
              var violations = validator.validate(action);
              if (violations.isEmpty()) {
                return action;
              } else {
                throw new PipelineValidationException(
                    String.format(
                        "Validation failed: %s",
                        violations.stream()
                            .map(v -> String.format("%s: %s", v.getPropertyPath(), v.getMessage()))
                            .sorted()
                            .collect(Collectors.joining(", "))));
              }
            })
        .flatMap(a -> Mono.from(delegate.set(context, stateType, a)));
  }
}
