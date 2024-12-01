package com.ratifire.devrate.security.model.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum representing various authentication errors and their associated HTTP statuses.
 */
@Getter
public enum AuthenticationError {

  TOKEN_EXPIRED("token_expired", 498),
  UNAUTHORIZED("unauthorized", HttpStatus.UNAUTHORIZED.value());

  private final String error;
  private final int httpStatus;

  AuthenticationError(String error, int httpStatus) {
    this.error = error;
    this.httpStatus = httpStatus;
  }
}