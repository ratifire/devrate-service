package com.ratifire.devrate.security.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for AWS Cognito as an OAuth2 provider.
 */
@Configuration
@ConfigurationProperties("spring.security.oauth2.client.provider.cognito")
@Getter
@Setter
public class CognitoProviderProperties {

  private String issuerUri;
  private String jwkSetUri;
}