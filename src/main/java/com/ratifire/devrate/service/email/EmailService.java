package com.ratifire.devrate.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Service implementation for sending emails using JavaMailSender.
 */
@AllArgsConstructor
@Service
public class EmailService implements EmailSender {

  private static final Logger log = LogManager.getLogger(EmailService.class);

  private JavaMailSender mailSender;

  /**
   * Sends an email with the specified content to the provided email address.
   *
   * @param subject   The email subject.
   * @param to   The email address to which the email should be sent.
   * @param text The message text.
   * @param html {@code true} the param 'text' will be html content
   *             {@code false} the param 'text' will be plain text content
   * @throws IllegalStateException If an error occurs while sending the email.
   */
  public void sendEmail(String subject, String to, String text, boolean html) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
      helper.setText(text, html);
      helper.setTo(to);
      helper.setSubject(subject);

      mailSender.send(mimeMessage);

      log.info("The email sent successfully to '{}'. Subject: '{}'", to, subject);
    } catch (MessagingException ex) {
      log.error("Failed to send the email to '{}'. Subject: '{}'", to, subject, ex);
      throw new IllegalStateException("failed to send email");
    }
  }
}
