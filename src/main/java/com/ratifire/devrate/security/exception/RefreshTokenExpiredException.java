package com.ratifire.devrate.security.exception;

/**
 * Exception thrown when a refresh token has expired.
 */
public class RefreshTokenExpiredException extends RuntimeException {

  public RefreshTokenExpiredException(String message) {
    super(message);
  }

  public RefreshTokenExpiredException(String message, Throwable cause) {
    super(message, cause);
  }
}