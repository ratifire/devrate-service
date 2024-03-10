package com.ratifire.devrate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.exception.WebSocketSessionNotFoundException;
import com.ratifire.devrate.service.WebSocketSessionRegistry;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.WebSocketSession;

/**
 * Unit tests for the {@link NotificationController} class.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class NotificationControllerTest {

  @Mock
  private WebSocketSessionRegistry sessionRegistry;

  @Mock
  private WebSocketSession session;

  @InjectMocks
  private NotificationController notificationController;

  @Test
  void sendNotificationToUser_Success() throws IOException {
    String login = "testUser";
    String notificationText = "Test notification";
    Notification notification = Notification.builder()
        .text(notificationText)
        .build();

    when(sessionRegistry.getSession(login)).thenReturn(session);

    notificationController.sendNotificationToUser(login, notification);

    verify(session).sendMessage(any());
  }

  @Test
  void sendNotificationToUser_SessionNotFound() {
    String login = "nonexistentUser";

    when(sessionRegistry.getSession(login)).thenThrow(
        new WebSocketSessionNotFoundException("Session not found"));
  }
}
