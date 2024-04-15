package com.ratifire.devrate.util.websocket;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.UserSecurityService;
import java.io.IOException;
import java.util.List;
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
public class WebSocketSenderTest {

  @Mock
  private WebSocketSessionRegistry sessionRegistry;

  @Mock
  private NotificationService notificationService;

  @Mock
  private UserSecurityService userSecurityService;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private WebSocketSession session;

  @InjectMocks
  private WebSocketSender webSocketSender;

  @Test
  void testSendNotificationsByUserId() throws IOException {
    UserSecurity userSecurity = UserSecurity.builder()
        .email("test@example.com")
        .build();
    String testMessage = "{test message}";
    List<NotificationDto> notifications = List.of(NotificationDto.builder().build());
    Set<WebSocketSession> sessions = Set.of(session);

    when(userSecurityService.getByUserId(anyLong())).thenReturn(userSecurity);
    when(notificationService.getAllByEmail(any())).thenReturn(notifications);
    when(sessionRegistry.getUserSessions(any())).thenReturn(sessions);
    when(objectMapper.writeValueAsString(any())).thenReturn(testMessage);

    webSocketSender.sendNotificationsByUserId(1L);

    verify(session, times(1)).sendMessage(any(TextMessage.class));
  }

  @Test
  void testSendNotificationsBySession() throws IOException {
    String login = "testUser";
    String testMessage = "{test message}";
    List<NotificationDto> notifications = List.of(NotificationDto.builder().build());

    when(notificationService.getAllByEmail(any())).thenReturn(notifications);
    when(objectMapper.writeValueAsString(any())).thenReturn(testMessage);

    webSocketSender.sendNotificationsBySession(login, session);

    verify(session, times(1)).sendMessage(any(TextMessage.class));
  }
}
