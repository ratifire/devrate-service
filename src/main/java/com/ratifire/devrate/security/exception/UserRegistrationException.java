package com.ratifire.devrate.security.exception;

/**
 * Exception thrown when an error occurs during the user registration process.
 */
public class UserRegistrationException extends RuntimeException {
  public UserRegistrationException(String message) {
    super(message);
  }
}