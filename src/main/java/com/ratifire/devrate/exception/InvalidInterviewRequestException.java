package com.ratifire.devrate.exception;

/**
 * Exception thrown when an interview summary is not found.
 */
public class InvalidInterviewRequestException extends RuntimeException {
  public InvalidInterviewRequestException(String message) {
    super(message);
  }
}
