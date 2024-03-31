package com.ratifire.devrate.util.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.NotificationActionDto;
import com.ratifire.devrate.enums.NotificationAction;
import com.ratifire.devrate.exception.ActionNotSupportedException;
import com.ratifire.devrate.exception.LoginNotFoundException;
import com.ratifire.devrate.exception.WebSocketInvalidMessageFormatException;
import com.ratifire.devrate.service.NotificationService;
import java.security.Principal;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * Controller class for handling WebSocket connections.
 */
@Component
@AllArgsConstructor
public class WebSocketHandler extends AbstractWebSocketHandler {

  private final WebSocketSessionRegistry sessionRegistry;
  private final NotificationService notificationService;
  private final WebSocketSender webSocketSender;
  private final ObjectMapper objectMapper;

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    String login = extractLogin(session);
    sessionRegistry.registerSession(login, session);

    webSocketSender.sendNotificationsBySession(login, session);
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session,
      @NonNull CloseStatus closeStatus) {
    String login = extractLogin(session);
    sessionRegistry.closeRemoveSession(login, session);
  }

  @Override
  public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
    NotificationActionDto action = getAction(message.getPayload());
    handleAction(action);

    String login = extractLogin(session);
    webSocketSender.sendNotificationsByLogin(login);
  }

  /**
   * Handles the received notification action.
   *
   * @param actionDto the notification action to handle
   * @throws ActionNotSupportedException if the action is not supported
   */
  private void handleAction(NotificationActionDto actionDto) {
    long notificationId = actionDto.getNotificationId();
    switch (NotificationAction.fromValue(actionDto.getAction())) {
      case READ -> notificationService.readNotificationById(notificationId);
      case DELETE -> notificationService.deleteNotificationById(notificationId);
      case null -> throw new ActionNotSupportedException(actionDto.getAction());
    }
  }

  /**
   * Parses the incoming text message payload into a NotificationActionDto object.
   *
   * @param payload the payload of the incoming text message
   * @return the parsed NotificationActionDto object
   * @throws WebSocketInvalidMessageFormatException if the payload is not in the expected format
   */
  private NotificationActionDto getAction(String payload) {
    try {
      return objectMapper.readValue(payload, NotificationActionDto.class);
    } catch (JsonProcessingException e) {
      throw new WebSocketInvalidMessageFormatException(payload);
    }
  }

  /**
   * Extracts the login associated with the given WebSocket session.
   *
   * @param session the WebSocket session
   * @return the login associated with the session
   * @throws LoginNotFoundException if the login is not found in the session
   */
  private String extractLogin(WebSocketSession session) {
    Principal principal = session.getPrincipal();
    if (principal == null) {
      throw new LoginNotFoundException("Login not found.");
    }
    return principal.getName();
  }
}
