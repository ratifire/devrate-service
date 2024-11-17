package com.ratifire.devrate.security.exception;

/**
 * This exception is thrown to indicate authentication failures in the application.
 */
public class AuthenticationException extends RuntimeException {

  public AuthenticationException(String message) {
    super(message);
  }
}
