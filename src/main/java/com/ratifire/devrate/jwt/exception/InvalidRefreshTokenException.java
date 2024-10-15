package com.ratifire.devrate.jwt.exception;

/**
 * test.
 */
public class InvalidRefreshTokenException extends RuntimeException {

  public InvalidRefreshTokenException(String message) {
    super(message);
  }
}