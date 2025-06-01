package com.ratifire.devrate.security.exception;

/**
 * Exception thrown when there is an error during token generation.
 */
public class TokenGenerationException extends RuntimeException {

  public TokenGenerationException(String message) {
    super(message);
  }

  public TokenGenerationException(String message, Throwable cause) {
    super(message, cause);
  }
}