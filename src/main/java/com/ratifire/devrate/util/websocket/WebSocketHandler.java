package com.ratifire.devrate.util.websocket;

import com.ratifire.devrate.exception.EmailNotFoundException;
import java.security.Principal;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * Controller class for handling WebSocket connections.
 */
@Component
@AllArgsConstructor
public class WebSocketHandler extends AbstractWebSocketHandler {

  private final WebSocketSessionRegistry sessionRegistry;
  private final WebSocketSender webSocketSender;

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    String email = extractEmail(session);
    sessionRegistry.registerSession(email, session);
    webSocketSender.sendNotificationsBySession(email, session);
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session,
      @NonNull CloseStatus closeStatus) {
    String email = extractEmail(session);
    sessionRegistry.closeRemoveSession(email, session);
  }

  /**
   * Extracts the email associated with the given WebSocket session.
   *
   * @param session the WebSocket session
   * @return the email associated with the session
   * @throws EmailNotFoundException if the email is not found in the session
   */
  private String extractEmail(WebSocketSession session) {
    Principal principal = session.getPrincipal();
    if (principal == null) {
      throw new EmailNotFoundException("Login not found.");
    }
    return principal.getName();
  }
}
