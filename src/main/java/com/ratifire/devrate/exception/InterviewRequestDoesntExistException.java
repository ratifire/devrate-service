package com.ratifire.devrate.exception;

/**
 * Exception thrown when an interview request doesn't exist.
 */
public class InterviewRequestDoesntExistException extends RuntimeException {

  private static final String MESSAGE_ID_NOT_FOUND = "Interview request for user with ID %d "
      + "and role %s doesn't exist.";

  public InterviewRequestDoesntExistException(long userId, String role) {
    super(String.format(MESSAGE_ID_NOT_FOUND, userId, role));
  }
}