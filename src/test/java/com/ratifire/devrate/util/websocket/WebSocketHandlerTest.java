package com.ratifire.devrate.util.websocket;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.NotificationActionDto;
import com.ratifire.devrate.exception.ActionNotSupportedException;
import com.ratifire.devrate.exception.LoginNotFoundException;
import com.ratifire.devrate.exception.WebSocketInvalidMessageFormatException;
import com.ratifire.devrate.service.NotificationService;
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
  @Mock
  private TextMessage textMessage;
  @Mock
  private NotificationService notificationService;
  @Mock
  private WebSocketSender webSocketSender;
  @Mock
  private ObjectMapper objectMapper;
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
  void handleTextMessage_ReadNotificationById() throws Exception {
    NotificationActionDto actionDto = NotificationActionDto.builder()
        .notificationId(1L)
        .action("read")
        .build();
    String testMessage = "{test message}";

    when(textMessage.getPayload()).thenReturn(testMessage);
    when(objectMapper.readValue(anyString(), eq(NotificationActionDto.class))).thenReturn(
        actionDto);
    doNothing().when(notificationService).readNotificationById(anyLong());
    when(session.getPrincipal()).thenReturn(principal);
    when(principal.getName()).thenReturn(testLogin);
    doNothing().when(webSocketSender).sendNotificationsByLogin(anyString());

    webSocketHandler.handleTextMessage(session, textMessage);

    verify(webSocketSender, times(1)).sendNotificationsByLogin(testLogin);
  }

  @Test
  void handleTextMessage_deleteNotificationById() throws Exception {
    NotificationActionDto actionDto = NotificationActionDto.builder()
        .notificationId(1L)
        .action("delete")
        .build();
    String testMessage = "{test message}";

    when(textMessage.getPayload()).thenReturn(testMessage);
    when(objectMapper.readValue(anyString(), eq(NotificationActionDto.class))).thenReturn(
        actionDto);
    doNothing().when(notificationService).deleteNotificationById(anyLong());
    when(session.getPrincipal()).thenReturn(principal);
    when(principal.getName()).thenReturn(testLogin);
    doNothing().when(webSocketSender).sendNotificationsByLogin(anyString());

    webSocketHandler.handleTextMessage(session, textMessage);

    verify(webSocketSender, times(1)).sendNotificationsByLogin(testLogin);
  }

  @Test
  void handleTextMessage_throwWebSocketInvalidMessageFormatException()
      throws JsonProcessingException {
    String testMessage = "{test message}";
    when(textMessage.getPayload()).thenReturn(testMessage);
    doThrow(JsonProcessingException.class).when(objectMapper)
        .readValue(anyString(), eq(NotificationActionDto.class));

    assertThrows(WebSocketInvalidMessageFormatException.class,
        () -> webSocketHandler.handleTextMessage(session, textMessage));
  }

  @Test
  void handleTextMessage_throwActionNotSupportedException()
      throws Exception {
    NotificationActionDto actionDto = NotificationActionDto.builder()
        .notificationId(1L)
        .action("not valid")
        .build();
    String testMessage = "{test message}";
    when(textMessage.getPayload()).thenReturn(testMessage);
    when(objectMapper.readValue(anyString(), eq(NotificationActionDto.class))).thenReturn(
        actionDto);

    assertThrows(ActionNotSupportedException.class,
        () -> webSocketHandler.handleTextMessage(session, textMessage));
  }

}
