package io.activated.pipeline.env;

import java.security.Principal;
import java.util.Optional;
import java.util.function.Supplier;

public interface PrincipalSupplier extends Supplier<Optional<Principal>> {}
