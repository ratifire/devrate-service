package com.ratifire.devrate.util.websocket;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.exception.EmailNotFoundException;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

/**
 * Unit tests for the {@link WebSocketHandler} class.
 */
@ExtendWith(MockitoExtension.class)
public class WebSocketHandlerTest {

  @Mock
  private Principal principal;
  @Mock
  private WebSocketSessionRegistry sessionRegistry;
  @Mock
  private WebSocketSession session;
  @Mock
  private WebSocketSender webSocketSender;
  @InjectMocks
  private WebSocketHandler webSocketHandler;

  private final String testLogin = "test@example.com";

  @Test
  void afterConnectionEstablished_LoginFound_CallsRegisterSession() {
    when(session.getPrincipal()).thenReturn(principal);
    when(principal.getName()).thenReturn(testLogin);
    doNothing().when(sessionRegistry).registerSession(any(), any());
    doNothing().when(webSocketSender).sendNotificationsBySession(any(), any());

    webSocketHandler.afterConnectionEstablished(session);

    verify(sessionRegistry, times(1)).registerSession(testLogin, session);
  }

  @Test
  void afterConnectionEstablished_LoginNotFound_ThrowsLoginNotFoundException() {
    when(session.getPrincipal()).thenReturn(null);

    assertThrows(EmailNotFoundException.class, () ->
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

    assertThrows(EmailNotFoundException.class, () ->
        webSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL));
  }
}
