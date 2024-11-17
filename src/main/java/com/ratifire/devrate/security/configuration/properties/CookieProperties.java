package com.ratifire.devrate.security.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for cookies.
 */
@Configuration
@ConfigurationProperties("cookie")
@FieldNameConstants
@Getter
@Setter
public class CookieProperties {

  private int lifeTime;
}