package com.ratifire.devrate.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.util.WebSocketSessionRegistry;
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
  private WebSocketSession session;

  @InjectMocks
  private WebSocketSender webSocketSender;

  @Test
  void testSendMessageByUserEmail() throws IOException {
    NotificationDto notification = NotificationDto.builder()
        .payload("{test message}")
        .build();

    String login = "test@email.com";
    when(sessionRegistry.getUserSessions(login)).thenReturn(Set.of(session));

    webSocketSender.sendNotificationByUserEmail(notification, login);

    verify(session, times(1)).sendMessage(any(TextMessage.class));
  }
}
