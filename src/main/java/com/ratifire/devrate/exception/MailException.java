package com.ratifire.devrate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * MailException represents an exception that occurs during email sending or handling.
 * Use HttpStatus field to represent the status of the exception.
 */
@Getter
public class MailException extends RuntimeException {

  private final HttpStatus status;

  public MailException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }
}
