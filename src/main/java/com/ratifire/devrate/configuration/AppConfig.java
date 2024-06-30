package com.ratifire.devrate.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Common class for configuring the application.
 */
@Configuration
public class AppConfig {

  /**
   * Create a {@link RestTemplate} bean for sending HTTP requests to the external API.
   * !!!Attention!!! Potential bottleneck.
   * {@link RestTemplate} is used for making synchronous HTTP requests, where all requests are
   * processed sequentially. For asynchronous requests it is better to use WebClient.
   *
   * @return {@link RestTemplate} instance
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
