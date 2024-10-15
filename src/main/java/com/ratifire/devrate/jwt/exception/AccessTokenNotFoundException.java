package com.ratifire.devrate.jwt.exception;

/**
 * test.
 */
public class AccessTokenNotFoundException extends RuntimeException {

  public AccessTokenNotFoundException(String message) {
    super(message);
  }
}