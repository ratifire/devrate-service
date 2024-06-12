package com.ratifire.devrate.util.zoom.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Configuration class for Zoom OAuth2 authentication response details.
 */
@Data
public class ZoomAuthResponse {

  @JsonProperty(value = "access_token")
  private String accessToken;

  @JsonProperty(value = "token_type")
  private String tokenType;

  @JsonProperty(value = "expires_in")
  private Long expiresIn;    // The duration in seconds that the access token is valid

  private String scope;
}
