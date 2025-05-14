package com.ratifire.devrate.security.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for OAuth2.
 */
@Configuration
@ConfigurationProperties("oauth2")
@FieldNameConstants
@Getter
@Setter
public class Oauth2Properties {

  private String stateSecret;
  private int stateLifeTime;
}