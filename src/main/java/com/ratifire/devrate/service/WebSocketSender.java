package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.util.JsonConverter;
import com.ratifire.devrate.util.WebSocketSessionRegistry;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Component for sending notification over WebSocket.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketSender {

  private final WebSocketSessionRegistry sessionRegistry;

  /**
   * Sends notification to all WebSocket sessions.
   *
   * @param notification NotificationDto objects to be sent.
   * @param email        The email address of the user to receive the notification.
   */
  public void sendNotificationByUserEmail(NotificationDto notification, String email) {
    Set<WebSocketSession> sessions = sessionRegistry.getUserSessions(email);
    TextMessage message = new TextMessage(JsonConverter.serialize(notification));
    for (WebSocketSession session : sessions) {
      try {
        session.sendMessage(message);
      } catch (IOException e) {
        log.error("Failed to send message: {}", message, e);
      }
    }
  }
}
