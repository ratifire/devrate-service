package com.ratifire.devrate.security.configuration.properties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for AWS Cognito registration within the OAuth2 client setup.
 */
@Configuration
@ConfigurationProperties("spring.security.oauth2.client.registration.cognito")
@FieldNameConstants
@Getter
@Setter
public class CognitoRegistrationProperties {

  private String clientName;
  private String clientId;
  private String clientSecret;
  private List<String> scope;

}