package com.ratifire.devrate.util.zoom.webhook.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a response containing plain and encrypted tokens.
 */
@Data
@AllArgsConstructor
public class TokenResponse {
  private String plainToken;
  private String encryptedToken;
}