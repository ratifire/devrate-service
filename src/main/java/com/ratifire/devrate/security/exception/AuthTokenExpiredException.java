package com.ratifire.devrate.security.exception;

/**
 * Exception indicating that a token has expired.
 */
public class AuthTokenExpiredException extends RuntimeException {

  public AuthTokenExpiredException(String message) {
    super(message);
  }
}