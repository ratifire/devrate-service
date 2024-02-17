package com.ratifire.devrate.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;

/**
 * Configuration for integrating with MailHog in a local environment.
 */
@Configuration
@Profile("local")
public class MailHogConfiguration {

  /**
    * Creates and configures a {@link SimpleMailMessage} template to be used as a base for
    * sending emails throughout the application.
    */
  @Bean
  public SimpleMailMessage emailTemplate() {

    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("ratifire@devrate.com");

    return message;
  }
}
