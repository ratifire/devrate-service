package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user attempts to join an interview meeting earlier than the allowed.
 */
public class InterviewJoinTooEarlyException extends RuntimeException {

  private static final String MESSAGE = "Joining is only permitted 5 minutes prior to the interview"
      + " starting.";

  public InterviewJoinTooEarlyException() {
    super(MESSAGE);
  }
}
