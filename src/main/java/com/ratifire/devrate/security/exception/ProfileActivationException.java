package com.ratifire.devrate.security.exception;

/**
 * Exception thrown when an error occurs during the profile activation process.
 */
public class ProfileActivationException extends RuntimeException {
  public ProfileActivationException(String message) {
    super(message);
  }
}