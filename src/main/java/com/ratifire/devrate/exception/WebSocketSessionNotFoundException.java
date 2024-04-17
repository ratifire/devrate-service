package com.ratifire.devrate.exception;

/**
 * Exception thrown when a WebSocket session is not found or is not open.
 */
public class WebSocketSessionNotFoundException extends ResourceNotFoundException {
  public WebSocketSessionNotFoundException(String message) {
    super(message);
  }
}
