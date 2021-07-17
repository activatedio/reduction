package io.activated.pipeline.server.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SpringSessionIdSourceTest {

  private final String headerName = "test-header-name";
  private final String sessionId = "test-session-id";
  private SpringSessionIdSource unit;
  @Mock private HttpServletRequest request;

  @BeforeEach
  public void setUp() {
    unit = new SpringSessionIdSource(request);
  }

  @Test
  public void get_NoHeader() {
    assertThat(unit.get()).isNull();
  }

  @Test
  public void get() {
    when(request.getAttribute(Constants.SESSION_ID_ATTRIBUTE_NAME)).thenReturn(sessionId);
    assertThat(unit.get()).isEqualTo(sessionId);
  }
}
