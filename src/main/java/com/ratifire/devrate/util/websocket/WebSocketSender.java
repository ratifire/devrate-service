package com.ratifire.devrate.util.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.service.NotificationService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
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
  private final NotificationService notificationService;
  private final ObjectMapper objectMapper;
  private static final Logger logger = LoggerFactory.getLogger(WebSocketSender.class);

  /**
   * Sends notifications to all WebSocket sessions associated with a user's email.
   *
   * @param email The user's email.
   */
  public void sendNotificationsByUserEmail(String email) {
    List<NotificationDto> notifications = notificationService.getAllByEmail(email);
    Set<WebSocketSession> sessions = sessionRegistry.getUserSessions(email);
    for (WebSocketSession session : sessions) {
      sendNotifications(session, notifications);
    }
  }

  /**
   * Sends notifications to a specific WebSocket session associated with a user's login.
   *
   * @param email   The user's login.
   * @param session The WebSocket session to send notifications to.
   */
  public void sendNotificationsBySession(String email, WebSocketSession session) {
    List<NotificationDto> notifications = notificationService.getAllByEmail(email);
    sendNotifications(session, notifications);
  }

  /**
   * Sends a list of notifications to a WebSocket session.
   *
   * @param session       The WebSocket session to send notifications to.
   * @param notifications The list of notifications to send.
   */
  private void sendNotifications(WebSocketSession session, List<NotificationDto> notifications) {
    for (NotificationDto notification : notifications) {
      send(session, notification);
    }
  }

  /**
   * Adds a notification for the given user with the specified text.
   *
   * @param text   The text of the notification.
   * @param userId The ID of the user to add the notification for.
   */
  public void addNotification(String text, long userId) {
    Notification notification = Notification.builder()
        .text(text)
        .read(false)
        .userId(userId)
        .createdAt(LocalDateTime.now())
        .build();

    notificationService.save(notification);
  }

  /**
   * Adds a greeting notification for the given user.
   *
   * @param userId The ID of the user to add the greeting notification for.
   */
  public void addGreetingNotification(long userId) {
    String text = """
        Welcome aboard!
        We're thrilled to have you join DevRate community.
                
        At DevRate, we're all about empowering developers like you to share your expertise,
        learn from others, and build meaningful connections.
                
        Happy interviewing!
                
        Best regards,
        DevRate""";

    addNotification(text, userId);
  }

  /**
   * Sends a notification to a WebSocket session.
   *
   * @param session      The WebSocket session to send the notification to.
   * @param notification The notification to send.
   */
  private void send(WebSocketSession session, NotificationDto notification) {
    try {
      TextMessage message = new TextMessage(notificationToString(notification));
      session.sendMessage(message);
      logger.info("Notification sent successfully: {}", notification);
    } catch (IOException e) {
      logger.error("Failed to send notification: {}", notification, e);
    }
  }

  /**
   * Converts a notification object to a JSON string.
   *
   * @param notification The notification to convert.
   * @return The JSON string representation of the notification.
   */
  private String notificationToString(NotificationDto notification) {
    try {
      return objectMapper.writeValueAsString(notification);
    } catch (JsonProcessingException e) {
      logger.error("Failed to convert notification to JSON: {}", notification, e);
      return StringUtils.EMPTY;
    }
  }
}
