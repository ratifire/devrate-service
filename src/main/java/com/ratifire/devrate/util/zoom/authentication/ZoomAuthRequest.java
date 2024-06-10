package com.ratifire.devrate.util.zoom.authentication;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration class for Zoom OAuth2 authentication request details.
 */
@Component
@Data
public class ZoomAuthRequest {

  @Value("${zoom.oauth2.client-id}")
  private String zoomClientId;

  @Value("${zoom.oauth2.client-secret}")
  private String zoomClientSecret;

  @Value("${zoom.oauth2.issuer}")
  private String zoomIssuerUrl;

  @Value("${zoom.oauth2.account-id}")
  private String zoomAccountId;
}
