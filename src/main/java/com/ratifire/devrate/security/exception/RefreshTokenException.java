package com.ratifire.devrate.security.exception;

/**
 * Exception thrown when an error occurs during the refresh token process.
 */
public class RefreshTokenException extends RuntimeException {
  public RefreshTokenException(String message) {
    super(message);
  }
}