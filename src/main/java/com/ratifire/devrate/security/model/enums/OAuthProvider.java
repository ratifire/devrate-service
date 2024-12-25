package com.ratifire.devrate.security.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OAuthProvider {

  LINKEDIN("linkedIn"),
  GOOGLE("google");

  private final String provider;

}