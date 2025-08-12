package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user attempts to join an interview meeting earlier than the allowed.
 */
public class InterviewJoinTooEarlyException extends RuntimeException {
  public InterviewJoinTooEarlyException() {
    super("Joining is only permitted 5 minutes prior to the interview starting.");
  }
}
