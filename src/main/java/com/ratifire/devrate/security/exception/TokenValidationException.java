package com.ratifire.devrate.security.exception;

/**
 * Exception thrown when token validation fails.
 */
public class TokenValidationException extends RuntimeException {

  public TokenValidationException(String message) {
    super(message);
  }
}