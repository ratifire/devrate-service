package com.ratifire.devrate.jwt.exception;

/**
 * test.
 */
public class RefreshTokenNotFoundException extends RuntimeException {

  public RefreshTokenNotFoundException(String message) {
    super(message);
  }
}