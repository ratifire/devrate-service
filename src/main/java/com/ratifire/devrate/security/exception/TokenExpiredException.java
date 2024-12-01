package com.ratifire.devrate.security.exception;

/**
 * Exception indicating that a token has expired.
 */
public class TokenExpiredException extends RuntimeException {

  public TokenExpiredException(String message) {
    super(message);
  }
}