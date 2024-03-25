package com.ratifire.devrate.util.websocket;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.exception.LoginNotFoundException;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
  void afterConnectionEstablished_LoginFound_CallsRegisterSession() {
    when(session.getPrincipal()).thenReturn(principal);
    when(principal.getName()).thenReturn(testLogin);
    doNothing().when(sessionRegistry).registerSession(any(), any());

    webSocketHandler.afterConnectionEstablished(session);

    verify(sessionRegistry, times(1)).registerSession(testLogin, session);
  }

  @Test
  void afterConnectionEstablished_LoginNotFound_ThrowsLoginNotFoundException() {
    when(session.getPrincipal()).thenReturn(null);

    assertThrows(LoginNotFoundException.class, () ->
        webSocketHandler.afterConnectionEstablished(session));
  }

  @Test
  void afterConnectionClosed_LoginFound_CallsCloseRemoveSession() {
    when(session.getPrincipal()).thenReturn(principal);
    when(principal.getName()).thenReturn(testLogin);
    doNothing().when(sessionRegistry).closeRemoveSession(any(), any());

    webSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

    verify(sessionRegistry, times(1)).closeRemoveSession(testLogin, session);
  }

  @Test
  void afterConnectionClosed_LoginNotFound_ThrowsLoginNotFoundException() {
    when(session.getPrincipal()).thenReturn(null);

    assertThrows(LoginNotFoundException.class, () ->
        webSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL));
  }

  @Test
  void handleTextMessage_MessageSentToSession() throws Exception {
    TextMessage message = new TextMessage("Hello, world!");

    webSocketHandler.handleTextMessage(session, message);

    verify(session).sendMessage(message);
  }
}
