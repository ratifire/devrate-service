package com.ratifire.devrate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.service.WebSocketSessionRegistry;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

/**
 * Unit tests for the {@link WebSocketController} class.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class WebSocketControllerTest {

  @Mock
  private SecurityContext securityContext;
  @Mock
  private Authentication authentication;
  @Mock
  private WebSocketSessionRegistry sessionRegistry;

  @Mock
  private WebSocketSession session;

  @InjectMocks
  private WebSocketController webSocketController;

  private final String testLogin = "test@example.com";

  @Test
  void afterConnectionEstablished_WithAuthenticatedUser() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn(testLogin);
    when(session.getAttributes()).thenReturn(new HashMap<>());

    webSocketController.afterConnectionEstablished(session);

    verify(session, times(1)).getAttributes();
    verify(sessionRegistry, times(1)).registerSession(testLogin, session);
  }

  @Test
  void afterConnectionEstablished_WithUnauthenticatedUser() {
    when(securityContext.getAuthentication()).thenReturn(null);

    webSocketController.afterConnectionEstablished(session);

    verify(sessionRegistry, never()).registerSession(anyString(), any());
  }

  @Test
  void testAfterConnectionClosed_LoginPresent() {
    HashMap<String, Object> testAttributes = new HashMap<>();
    testAttributes.put("login", testLogin);
    when(session.getAttributes()).thenReturn(testAttributes);

    webSocketController.afterConnectionClosed(session, CloseStatus.NORMAL);

    verify(session, times(1)).getAttributes();
    verify(sessionRegistry, times(1)).removeSession(testLogin);
  }

  @Test
  void testAfterConnectionClosed_LoginNotPresent() {
    HashMap<String, Object> testAttributes = new HashMap<>();
    when(session.getAttributes()).thenReturn(testAttributes);

    webSocketController.afterConnectionClosed(session, CloseStatus.NORMAL);

    verify(session, times(1)).getAttributes();
    verify(sessionRegistry, never()).removeSession(testLogin);
  }
}
