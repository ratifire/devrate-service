package com.ratifire.devrate.configuration;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Configuration for integrating with MailHog in a local environment.
 */
@Configuration
@Profile("local")
public class MailHogConfiguration {

  @Value("${mail.server.host}")
  private String mailHost;

  @Value("${mail.server.port}")
  private int mailPort;

  /**
    * Creates and configures a JavaMailSender bean for sending emails through MailHog.
    * Uses 1025 as the port, which are standard settings for MailHog.
    * Authentication is disabled since MailHog by default does not require authentication.
    *
    * @return Configured instance of JavaMailSender for use in the local development environment.
    */
  @Bean
  public JavaMailSender mailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(mailHost);
    mailSender.setPort(mailPort);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "false");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");

    return mailSender;
  }

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