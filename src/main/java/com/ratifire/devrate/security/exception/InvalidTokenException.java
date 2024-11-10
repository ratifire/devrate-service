package com.ratifire.devrate.security.exception;

/**
 * Exception thrown when a JWT token is invalid or cannot be parsed.
 */
public class InvalidTokenException extends RuntimeException {

  public InvalidTokenException(String message) {
    super(message);
  }
}