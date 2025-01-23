package com.ratifire.devrate.exception;

/**
 * Exception thrown when an interview request doesn't exist.
 */
public class InterviewRequestDoesntExistException extends RuntimeException {

  private static final String MESSAGE_ID_NOT_FOUND = "Interview request with ID %d for user ID %d "
      + "doesn't exist.";

  public InterviewRequestDoesntExistException(long id, long userId) {
    super(String.format(MESSAGE_ID_NOT_FOUND, id, userId));
  }
}