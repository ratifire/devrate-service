package com.ratifire.devrate.security.exception;

/**
 * Exception thrown when an error occurs during the generation of a secret hash.
 */
public class SecretHashGenerationException extends RuntimeException {

  public SecretHashGenerationException(String message) {
    super(message);
  }
}