package com.ratifire.devrate.security.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration representing different types of tokens used in AWS Cognito.
 */
@AllArgsConstructor
@Getter
public enum CognitoTypeToken {
  ACCESS_TOKEN("access_token"),
  ID_TOKEN("id_token"),
  REFRESH_TOKEN("refresh_token");

  private final String value;

}