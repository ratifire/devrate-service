package com.ratifire.devrate.util.zoom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for setting up Zoom-related beans in the Spring context.
 */
@Configuration
public class ZoomConfig {
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
