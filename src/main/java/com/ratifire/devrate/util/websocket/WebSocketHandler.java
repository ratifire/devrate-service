package com.ratifire.devrate.util.websocket;

import com.ratifire.devrate.exception.LoginNotFoundException;
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

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    String login = extractLogin(session);
    sessionRegistry.registerSession(login, session);
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session,
      @NonNull CloseStatus closeStatus) {
    String login = extractLogin(session);
    sessionRegistry.closeRemoveSession(login, session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, @NonNull TextMessage message)
      throws Exception {
    session.sendMessage(message);
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
