package com.ratifire.devrate.util.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.NotificationDto;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Component for sending notifications over WebSocket. This class provides methods for sending
 * notifications to specific WebSocket sessions or all sessions associated with a user.
 */
@Component
@RequiredArgsConstructor
public class WebSocketSender {

  private final WebSocketSessionRegistry sessionRegistry;
  private final ObjectMapper objectMapper;
  private static final Logger logger = LoggerFactory.getLogger(WebSocketSender.class);

  /**
   * Sends notifications to all WebSocket sessions.
   *
   * @param notifications A list of {@link NotificationDto} objects to be sent.
   * @param email         The email address of the user whose WebSocket sessions will receive the
   *                      notifications.
   */
  public void sendNotificationsByUserEmail(List<NotificationDto> notifications, String email) {
    Set<WebSocketSession> sessions = sessionRegistry.getUserSessions(email);
    for (WebSocketSession session : sessions) {
      sendNotifications(session, notifications);
    }
  }

  /**
   * Sends notifications to a specific WebSocket session.
   *
   * @param notifications A list of {@link NotificationDto} objects to be sent.
   * @param session       The WebSocket session to send notifications to.
   */
  @Transactional
  public void sendNotificationsBySession(List<NotificationDto> notifications,
      WebSocketSession session) {
    sendNotifications(session, notifications);
  }

  /**
   * Sends a notifications to a WebSocket session.
   *
   * @param session      The WebSocket session to send the notifications to.
   * @param notifications The notifications to send.
   */
  private void sendNotifications(WebSocketSession session, List<NotificationDto> notifications) {
    try {
      TextMessage message = new TextMessage(notificationsToString(notifications));
      session.sendMessage(message);
    } catch (IOException e) {
      logger.error("Failed to send notifications: {}", notifications, e);
    }
  }

  /**
   * Convert the notification objects to a JSON string.
   *
   * @param notifications The notifications to convert.
   * @return The JSON string representation of the notifications.
   */
  private String notificationsToString(List<NotificationDto> notifications) {
    try {
      return objectMapper.writeValueAsString(notifications);
    } catch (JsonProcessingException e) {
      logger.error("Failed to convert notifications to JSON: {}", notifications, e);
      return StringUtils.EMPTY;
    }
  }
}
