package com.ratifire.devrate.service;

import com.ratifire.devrate.exception.WebSocketSessionNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * Component class for managing WebSocket sessions.
 */
@Component
public class WebSocketSessionRegistry {

  private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

  /**
   * Registers a WebSocket session for the specified login.
   *
   * @param login   the login of the user
   * @param session the WebSocket session to register
   */
  public void registerSession(String login, WebSocketSession session) {
    sessions.put(login, session);
  }

  /**
   * Removes a WebSocket session for the specified login.
   *
   * @param login the login of the user
   */
  public void removeSession(String login) {
    closeSession(sessions.remove(login));
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
   * Gets the WebSocket session for the specified login.
   *
   * @param login the login of the user
   * @return the WebSocket session
   * @throws WebSocketSessionNotFoundException if the session is not found or not open
   */
  public WebSocketSession getSession(String login) {
    WebSocketSession session = sessions.get(login);
    if (isExistAndOpen(session)) {
      return session;
    }

    throw new WebSocketSessionNotFoundException(
        "WebSocket session not found or is not open for user: " + login);
  }

  /**
   * Checks if the WebSocket session exists and is open.
   *
   * @param session the WebSocket session to check
   * @return true if the session exists and is open, otherwise false
   */
  private boolean isExistAndOpen(WebSocketSession session) {
    return session != null && session.isOpen();
  }
}
