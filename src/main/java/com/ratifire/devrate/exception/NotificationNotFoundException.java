package com.ratifire.devrate.exception;

/**
 * Exception thrown when a notification is not found.
 */
public class NotificationNotFoundException extends RuntimeException {

  private static final String MESSAGE_ID_NOT_FOUND = "Notification with id %d not found.";

  public NotificationNotFoundException(long id) {
    super(String.format(MESSAGE_ID_NOT_FOUND, id));
  }
}
