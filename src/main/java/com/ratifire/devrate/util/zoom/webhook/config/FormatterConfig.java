package com.ratifire.devrate.util.zoom.webhook.config;

import java.util.Formatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for providing a {@link Formatter} bean.
 * <p>
 * This bean can be used to format strings in a thread-safe manner.
 * </p>
 */
@Configuration
public class FormatterConfig {

  /**
   * Provides a {@link Formatter} bean.
   *
   * @return a new instance of {@link Formatter}
   */
  @Bean
  public Formatter formatter() {
    return new Formatter();
  }
}
