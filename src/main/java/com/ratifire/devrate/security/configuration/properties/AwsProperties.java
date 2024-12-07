package com.ratifire.devrate.security.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for AWS credentials and region settings.
 */
@Configuration
@ConfigurationProperties("aws")
@FieldNameConstants
@Getter
@Setter
public class AwsProperties {

  private String accessKeyId;
  private String secretKey;
  private String region;

}