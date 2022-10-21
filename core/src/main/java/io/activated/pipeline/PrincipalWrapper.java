package io.activated.pipeline;

import java.security.Principal;

public interface PrincipalWrapper {

  Principal getPrincipal();

  String getSub();
}
