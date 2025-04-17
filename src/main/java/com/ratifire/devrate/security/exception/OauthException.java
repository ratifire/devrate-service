package com.ratifire.devrate.security.exception;

/**
 * Exception thrown when an error occurs during the OAuth authentication process.
 */
public class OauthException extends RuntimeException {

  public OauthException(String message, Throwable cause) {
    super(message, cause);
  }

  public OauthException(String message) {
    super(message);
  }
}