package com.ratifire.devrate.exception;

/**
 * Exception thrown when an action over notification is not supported.
 */
public class ActionNotSupportedException extends RuntimeException {

  private static final String MESSAGE_ACTION_NOT_SUPPORTED = "Action '%s' is not supported";

  public ActionNotSupportedException(String action) {
    super(String.format(MESSAGE_ACTION_NOT_SUPPORTED, action));
  }
}
