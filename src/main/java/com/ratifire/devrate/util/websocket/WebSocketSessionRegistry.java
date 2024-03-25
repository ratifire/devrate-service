package com.ratifire.devrate.util.websocket;

import com.ratifire.devrate.exception.WebSocketSessionNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * Component class for managing WebSocket sessions.
 */
@Component
public class WebSocketSessionRegistry {

  private final Map<String, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();

  /**
   * Registers a WebSocket session for the specified login.
   *
   * @param login   the login of the user
   * @param session the WebSocket session to register
   */
  public void registerSession(String login, WebSocketSession session) {
    Set<WebSocketSession> roomSessions = sessions.computeIfAbsent(login,
        k -> ConcurrentHashMap.newKeySet());
    roomSessions.add(session);
  }

  /**
   * Closes the specified WebSocket session associated with the given login and removes it from the
   * session registry.
   *
   * @param login   the login of the user whose session is to be removed
   * @param session the WebSocket session to close and remove
   */
  public void closeRemoveSession(String login, WebSocketSession session) {
    closeSession(session);
    Set<WebSocketSession> userSessions = sessions.get(login);
    if (userSessions != null) {
      userSessions.remove(session);
    }
  }

  /**
   * Closes the specified WebSocket session.
   *
   * @param session the WebSocket session to close
   */
  private void closeSession(WebSocketSession session) {
    if (isExistAndOpen(session)) {
      try {
        session.close();
      } catch (IOException e) {
        throw new WebSocketSessionNotFoundException("Can't close WebSocketSession " + session);
      }
    }
  }

  /**
   * Retrieves the WebSocket sessions associated with the specified user login.
   *
   * @param login the login of the user whose WebSocket sessions are to be retrieved
   * @return a set of WebSocket sessions associated with the specified user login.
   */
  public Set<WebSocketSession> getUserSessions(String login) {
    return sessions.get(login);
  }

  /**
   * Checks if the WebSocket session exists and is open.
   *
   * @param session the WebSocket session to check
   * @return true if the session exists and is open, otherwise false
   */
  public boolean isExistAndOpen(WebSocketSession session) {
    return session != null && session.isOpen();
  }
}
