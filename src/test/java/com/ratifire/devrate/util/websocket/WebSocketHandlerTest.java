package com.ratifire.devrate.util.websocket;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Unit tests for the {@link WebSocketHandler} class.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class WebSocketHandlerTest {

  @Mock
  private Principal principal;
  @Mock
  private WebSocketSessionRegistry sessionRegistry;
  @Mock
  private WebSocketSession session;
  @InjectMocks
  private WebSocketHandler webSocketHandler;

  private final String testLogin = "test@example.com";

  @Test
  void afterConnectionEstablished_UserAuthenticated_SessionRegistered() {
    Mockito.when(principal.getName()).thenReturn(testLogin);
    Mockito.when(session.getPrincipal()).thenReturn(principal);

    webSocketHandler.afterConnectionEstablished(session);

    verify(sessionRegistry).registerSession(testLogin, session);
  }

  @Test
  void afterConnectionEstablished_UserNotAuthenticated_SessionNotRegistered() {
    Mockito.when(session.getPrincipal()).thenReturn(null);

    webSocketHandler.afterConnectionEstablished(session);

    verify(sessionRegistry, Mockito.never()).registerSession(anyString(),
        Mockito.any(WebSocketSession.class));
  }

  @Test
  void afterConnectionClosed_UserAuthenticated_SessionRemoved() {
    Mockito.when(principal.getName()).thenReturn(testLogin);
    Mockito.when(session.getPrincipal()).thenReturn(principal);

    webSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

    verify(sessionRegistry).removeSession(testLogin);
  }

  @Test
  void afterConnectionClosed_UserNotAuthenticated_SessionNotRemoved() {
    Mockito.when(session.getPrincipal()).thenReturn(null);

    webSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

    verify(sessionRegistry, Mockito.never()).removeSession(Mockito.anyString());
  }

  @Test
  void handleTextMessage_MessageSentToSession() throws Exception {
    TextMessage message = new TextMessage("Hello, world!");

    webSocketHandler.handleTextMessage(session, message);

    verify(session).sendMessage(message);
  }
}
