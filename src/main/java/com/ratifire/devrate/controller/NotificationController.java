package com.ratifire.devrate.controller;

import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.service.WebSocketSessionRegistry;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Controller class for handling notifications.
 */
@Controller
@RequiredArgsConstructor
public class NotificationController {

  private final WebSocketSessionRegistry sessionRegistry;

  /**
   * Sends a notification to a specific user.
   *
   * @param login        the login of the user to send the notification to
   * @param notification the notification to send
   * @throws IOException if an I/O error occurs while sending the notification
   */
  @MessageMapping("/notify/{login}")
  public void sendNotificationToUser(@DestinationVariable String login, Notification notification)
      throws IOException {
    WebSocketSession session = sessionRegistry.getSession(login);
    TextMessage message = new TextMessage(notification.getText());
    session.sendMessage(message);
  }
}
