package com.ratifire.devrate.util.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.NotificationDto;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Component for sending notification over WebSocket. This class provides methods for sending
 * notification to specific WebSocket sessions or all sessions associated with a user.
 */
@Component
@RequiredArgsConstructor
public class WebSocketSender {

  private final WebSocketSessionRegistry sessionRegistry;
  private final ObjectMapper objectMapper;
  private static final Logger logger = LoggerFactory.getLogger(WebSocketSender.class);

  /**
   * Sends notification to all WebSocket sessions.
   *
   * @param notification {@link NotificationDto} objects to be sent.
   * @param email        The email address of the user whose WebSocket sessions will receive the
   *                     notification.
   */
  public void sendNotificationByUserEmail(NotificationDto notification, String email) {
    Set<WebSocketSession> sessions = sessionRegistry.getUserSessions(email);
    for (WebSocketSession session : sessions) {
      sendNotification(session, notification);
    }
  }

  /**
   * Sends a notifications to a WebSocket session.
   *
   * @param session      The WebSocket session to send the notifications to.
   * @param notification The notification to send.
   */
  private void sendNotification(WebSocketSession session, NotificationDto notification) {
    try {
      TextMessage message = new TextMessage(notificationToString(notification));
      session.sendMessage(message);
    } catch (IOException e) {
      logger.error("Failed to send notification: {}", notification, e);
    }
  }

  /**
   * Convert the notification object to a JSON string.
   *
   * @param notification The notification to convert.
   * @return The JSON string representation of the notification.
   */
  private String notificationToString(NotificationDto notification) {
    try {
      return objectMapper.writeValueAsString(notification);
    } catch (JsonProcessingException e) {
      logger.error("Failed to convert notifications to JSON: {}", notification, e);
      return StringUtils.EMPTY;
    }
  }
}
