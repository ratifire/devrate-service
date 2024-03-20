package com.ratifire.devrate.util.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.exception.WebSocketSessionNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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
  private WebSocketSession session;

  @Mock
  private WebSocketSession session2;

  @InjectMocks
  private WebSocketSessionRegistry sessionRegistry;

  private final String testLogin = "test@example.com";

  @Test
  public void testRegisterSession() {
    sessionRegistry.registerSession(testLogin, session);

    Set<WebSocketSession> roomSessions = sessionRegistry.getUserSessions(testLogin);
    assertTrue(roomSessions.contains(session));
  }

  @Test
  void closeRemoveSession_SessionExistsAndOpen_RemovesFromRegistryAndCloses() throws IOException {
    sessionRegistry.registerSession(testLogin, session);
    when(session.isOpen()).thenReturn(true);

    sessionRegistry.closeRemoveSession(testLogin, session);

    verify(session, times(1)).close();
    assert !sessionRegistry.getUserSessions(testLogin).contains(session);
  }

  @Test
  void closeRemoveSession_SessionNotExists_DoesNothing() throws IOException {
    sessionRegistry.closeRemoveSession(testLogin, session);

    verify(session, never()).close();
  }

  @Test
  void closeRemoveSession_SessionNotFound_ThrowsWebSocketSessionNotFoundException()
      throws IOException {
    when(session.isOpen()).thenReturn(true);
    doThrow(IOException.class).when(session).close();

    assertThrows(WebSocketSessionNotFoundException.class, () ->
        sessionRegistry.closeRemoveSession(testLogin, session));
  }

  @Test
  void getUserSessions_ReturnsCorrectSessions() {
    Set<WebSocketSession> expectedSessions = new HashSet<>();
    expectedSessions.add(session);
    expectedSessions.add(session2);
    sessionRegistry.registerSession(testLogin, session);
    sessionRegistry.registerSession(testLogin, session2);

    Set<WebSocketSession> actualSessions = sessionRegistry.getUserSessions(testLogin);

    assertEquals(expectedSessions, actualSessions);
  }

  @Test
  void getUserSessions_NoSessionsForLogin_ReturnsNull() {
    Set<WebSocketSession> actualSessions = sessionRegistry.getUserSessions(testLogin);

    assertNull(actualSessions);
  }

  @Test
  void isExistAndOpen_SessionExistsAndOpen_ReturnsTrue() {
    when(session.isOpen()).thenReturn(true);

    assertTrue(sessionRegistry.isExistAndOpen(session));
  }

  @Test
  void isExistAndOpen_SessionExistsButClosed_ReturnsFalse() {
    when(session.isOpen()).thenReturn(false);

    assertFalse(sessionRegistry.isExistAndOpen(session));
  }

  @Test
  void isExistAndOpen_SessionIsNull_ReturnsFalse() {
    assertFalse(sessionRegistry.isExistAndOpen(null));
  }
}
