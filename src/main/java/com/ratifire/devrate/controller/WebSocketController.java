package com.ratifire.devrate.controller;

import com.ratifire.devrate.service.WebSocketSessionRegistry;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Controller class for handling WebSocket connections.
 */
@Controller
@AllArgsConstructor
public class WebSocketController implements WebSocketHandler {

  private final WebSocketSessionRegistry sessionRegistry;

  /**
   * Handles actions to be performed after a new WebSocket connection is established. Registers the
   * user session in the session registry.
   *
   * @param session the WebSocket session
   */
  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      String login = authentication.getName();
      session.getAttributes().put("login", login);
      sessionRegistry.registerSession(login, session);
    }
  }

  /**
   * Handles incoming WebSocket messages.
   *
   * @param session the WebSocket session
   * @param message the incoming message
   */
  @Override
  public void handleMessage(@NonNull WebSocketSession session,
      @NonNull WebSocketMessage<?> message) {
  }

  /**
   * Handles errors that occur during WebSocket transport.
   *
   * @param session   the WebSocket session
   * @param exception the exception that occurred
   */
  @Override
  public void handleTransportError(@NonNull WebSocketSession session,
      @NonNull Throwable exception) {
  }

  /**
   * Handles actions to be performed after a WebSocket connection is closed. Removes the user
   * session from the session registry.
   *
   * @param session     the WebSocket session
   * @param closeStatus the close status
   */
  @Override
  public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus closeStatus) {
    String login = (String) session.getAttributes().get("login");
    if (login != null) {
      sessionRegistry.removeSession(login);
    }
  }

  /**
   * Indicates whether this handler supports partial messages.
   *
   * @return true if this handler supports partial messages, otherwise false
   */
  @Override
  public boolean supportsPartialMessages() {
    return false;
  }
}
