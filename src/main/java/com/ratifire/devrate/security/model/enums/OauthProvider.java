package com.ratifire.devrate.security.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration representing supported OAuth providers.
 */
@Getter
@AllArgsConstructor
public enum OauthProvider {

  LINKEDIN("linkedIn"),
  GOOGLE("google");

  private final String provider;

}