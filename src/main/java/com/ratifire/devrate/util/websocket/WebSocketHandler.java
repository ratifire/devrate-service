package com.ratifire.devrate.util.websocket;

import java.security.Principal;
import java.util.Optional;
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
    Optional<String> login = extractLogin(session);
    login.ifPresent(s -> sessionRegistry.registerSession(s, session));
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session,
      @NonNull CloseStatus closeStatus) {
    Optional<String> login = extractLogin(session);
    login.ifPresent(sessionRegistry::removeSession);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, @NonNull TextMessage message)
      throws Exception {
    session.sendMessage(message);
  }

  private Optional<String> extractLogin(WebSocketSession session) {
    Principal principal = session.getPrincipal();
    if (principal == null) {
      return Optional.empty();
    }
    return Optional.of(principal.getName());
  }
}
