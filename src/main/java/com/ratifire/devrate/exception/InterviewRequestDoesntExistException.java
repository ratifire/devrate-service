package com.ratifire.devrate.exception;

/**
 * Exception thrown when an interview request doesn't exist.
 */
public class InterviewRequestDoesntExistException extends RuntimeException {

  private static final String MESSAGE_ID_NOT_FOUND = "Interview request for user with ID %d, "
      + "role %s and mastery ID %d doesn't exist.";

  public InterviewRequestDoesntExistException(long userId, String role, long masteryId) {
    super(String.format(MESSAGE_ID_NOT_FOUND, userId, role, masteryId));
  }
}