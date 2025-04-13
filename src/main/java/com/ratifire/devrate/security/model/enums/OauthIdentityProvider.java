package com.ratifire.devrate.security.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration representing supported OAuth providers.
 */
@Getter
@AllArgsConstructor
public enum OauthIdentityProvider {

  LINKEDIN("LinkedIn"),
  GOOGLE("Google");

  private final String provider;

}