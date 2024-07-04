package com.ratifire.devrate.service.email;

import com.ratifire.devrate.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Service implementation for sending emails using JavaMailSender.
 */
@Service
@AllArgsConstructor
public class EmailService {

  private static final Logger log = LogManager.getLogger(EmailService.class);
  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  /**
   * Sends a confirmation code email to the user.
   *
   * @param email The user's email address.
   * @param code  The confirmation code.
   */
  public void sendConfirmationCodeEmail(String email, String code) {
    String subject = "Your DevRate Confirmation Code - " + code;

    Map<String, Object> variables = new HashMap<>();
    variables.put("email", email);
    variables.put("code", code);
    String text = buildTemplateEmailText("confirmation-email", variables);
    sendEmail(email, subject, text);
  }

  /**
   * Sends a welcome email to the user.
   *
   * @param user  The user object containing user information.
   * @param email The user's email address.
   */
  public void sendGreetingsEmail(User user, String email) {
    String subject = "Welcome to DevRate!";
    String text = buildTemplateEmailText("greeting-email", Map.of("user", user));
    sendEmail(email, subject, text);
  }

  /**
   * Sends an email with a password reset code to the user.
   *
   * @param email The user's email address.
   * @param code  The unique password reset code.
   */
  public void sendPasswordResetEmail(String email, String code) {
    String subject = "Password Reset";
    String text = buildTemplateEmailText("reset-password-email", Map.of("code", code));
    sendEmail(email, subject, text);
  }

  /**
   * Sends an email confirmation about the password change.
   *
   * @param email The user's email address.
   */
  public void sendPasswordChangeConfirmation(String email) {
    String subject = "Password Successfully Reset";
    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String text = "Your password has been successfully changed on " + formattedDateTime + ".";
    sendEmail(email, subject, text);
  }

  /**
   * Sends an email to notify the user that their interview request has expired.
   *
   * @param user  The user whose interview request has expired.
   * @param email The email address of the user.
   */
  public void sendInterviewRequestExpiryEmail(User user, String email) {
    String subject = "Interview Request Expired";
    String text = buildTemplateEmailText("interview-request-expired-email",
        Map.of("user", user));
    sendEmail(email, subject, text);
  }

  /**
   * Sends a simple email message.
   *
   * @param recipient The recipient's email address.
   * @param subject   The subject of the email.
   * @param text      The content of the email.
   */
  private void sendEmail(String recipient, String subject, String text) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
    try {
      helper.setFrom("ratifire@devrate.com");
      helper.setText(text, true);
      helper.setTo(recipient);
      helper.setSubject(subject);

      mailSender.send(mimeMessage);
    } catch (MessagingException e) {
      log.error("Failed to construct the email to '" + recipient + "'."
          + " Subject: '" + subject + "'", e);
    } catch (MailException e) {
      log.error("Failed to send the email to '" + recipient + "'."
          + " Subject: '" + subject + "'", e);
    }
  }

  /**
   * Builds an email text using a Thymeleaf template and provided variables.
   *
   * @param template  The name of the Thymeleaf template.
   * @param variables The variables to be passed to the template.
   * @return The processed email text.
   */
  private String buildTemplateEmailText(String template, Map<String, Object> variables) {
    Context context = new Context();
    for (Entry<String, Object> entry : variables.entrySet()) {
      context.setVariable(entry.getKey(), entry.getValue());
    }
    return templateEngine.process(template, context);
  }

  /**
   * Sends an email to notify recipient about interview rejection.
   *
   * @param recipientUser The user for whom the interview was rejected.
   * @param rejectionUser The user who rejected the interview.
   * @param scheduledTime The scheduled time of the interview.
   */
  public void sendInterviewRejectionMessage(User recipientUser, User rejectionUser,
      ZonedDateTime scheduledTime, String email) {
    Map<String, Object> model = new HashMap<>();
    model.put("recipientUser", recipientUser);
    model.put("rejectionUser", rejectionUser);
    model.put("scheduledTime", scheduledTime);

    String subject = "Interview Rejected";
    String text = buildTemplateEmailText("interview-rejected-email", model);
    sendEmail(email, subject, text);
  }
}
