package com.ratifire.devrate.util.websocket;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.NotificationDto;
import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Unit tests for the {@link WebSocketSender} class.
 */
@ExtendWith(MockitoExtension.class)
class WebSocketSenderTest {

  @Mock
  private WebSocketSessionRegistry sessionRegistry;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private WebSocketSession session;

  @InjectMocks
  private WebSocketSender webSocketSender;

  private final String testMessage = "{test message}";

  @Test
  void testSendNotificationByUserEmail() throws IOException {
    NotificationDto notification = NotificationDto.builder().build();

    when(objectMapper.writeValueAsString(any())).thenReturn(testMessage);
    String login = "test@email.com";
    when(sessionRegistry.getUserSessions(login)).thenReturn(Set.of(session));

    webSocketSender.sendNotificationByUserEmail(notification, login);

    verify(session, times(1)).sendMessage(any(TextMessage.class));
  }
}
