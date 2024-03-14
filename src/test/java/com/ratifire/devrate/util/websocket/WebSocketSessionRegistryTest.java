package com.ratifire.devrate.util.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.exception.WebSocketSessionNotFoundException;
import com.ratifire.devrate.util.websocket.WebSocketSessionRegistry;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

/**
 * Unit tests for the {@link WebSocketSessionRegistry} class.
 */
@ExtendWith(MockitoExtension.class)
public class WebSocketSessionRegistryTest {

  @Mock
  private WebSocketSession webSocketSession;

  @InjectMocks
  private WebSocketSessionRegistry webSocketSessionRegistry;

  private final String testLogin = "test@example.com";

  @Test
  public void testRegisterSession() {
    webSocketSessionRegistry.registerSession(testLogin, webSocketSession);
    when(webSocketSession.isOpen()).thenReturn(true);
    assertNotNull(webSocketSessionRegistry.getSession(testLogin));
  }

  @Test
  public void testRemoveSession() throws IOException {
    webSocketSessionRegistry.registerSession(testLogin, webSocketSession);
    when(webSocketSession.isOpen()).thenReturn(true);
    webSocketSessionRegistry.removeSession(testLogin);
    verify(webSocketSession, times(1)).close();
  }

  @Test
  public void testGetSession_PositiveScenario() {
    webSocketSessionRegistry.registerSession(testLogin, webSocketSession);
    when(webSocketSession.isOpen()).thenReturn(true);
    assertEquals(webSocketSession, webSocketSessionRegistry.getSession(testLogin));
  }

  @Test
  public void testGetSession_SessionNotFound() {
    assertThrows(WebSocketSessionNotFoundException.class,
        () -> webSocketSessionRegistry.getSession(testLogin));
  }

  @Test
  public void testGetSession_SessionNotOpen() {
    when(webSocketSession.isOpen()).thenReturn(false);
    webSocketSessionRegistry.registerSession(testLogin, webSocketSession);
    assertThrows(WebSocketSessionNotFoundException.class,
        () -> webSocketSessionRegistry.getSession(testLogin));
  }

  @Test
  public void testGetSessionNotFound() {
    assertThrows(WebSocketSessionNotFoundException.class,
        () -> webSocketSessionRegistry.getSession(testLogin));
  }

  @Test
  public void testGetSessionNotOpen() {
    when(webSocketSession.isOpen()).thenReturn(false);
    webSocketSessionRegistry.registerSession(testLogin, webSocketSession);
    assertThrows(WebSocketSessionNotFoundException.class,
        () -> webSocketSessionRegistry.getSession(testLogin));
  }
}
