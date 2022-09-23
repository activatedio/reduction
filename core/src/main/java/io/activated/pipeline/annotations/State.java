package io.activated.pipeline.annotations;

import io.activated.pipeline.StateGuard;
import io.activated.pipeline.key.KeyStrategy;
import io.activated.pipeline.key.PrincipalSessionKeyUpgradeStrategy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface State {
  Class<? extends StateGuard<?>>[] guards() default {};

  Class<? extends KeyStrategy> keyStrategy() default PrincipalSessionKeyUpgradeStrategy.class;
}
