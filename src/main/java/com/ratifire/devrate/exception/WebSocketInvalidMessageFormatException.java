package com.ratifire.devrate.exception;

/**
 * Exception thrown when the format of a WebSocket message is invalid.
 */
public class WebSocketInvalidMessageFormatException extends RuntimeException {

  private static final String MESSAGE_INVALID_PAYLOAD = "Can't convert '%s' into Dto.";

  public WebSocketInvalidMessageFormatException(String payload) {
    super(String.format(MESSAGE_INVALID_PAYLOAD, payload));
  }
}
