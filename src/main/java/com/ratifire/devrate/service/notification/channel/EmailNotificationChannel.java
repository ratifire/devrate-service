package com.ratifire.devrate.service.notification.channel;

import com.ratifire.devrate.service.notification.NotificationChannel;
import com.ratifire.devrate.service.notification.NotificationChannelType;
import com.ratifire.devrate.service.notification.model.NotificationRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Email notification channel implementation.
 * Handles sending notifications via email using templates.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationChannel implements NotificationChannel {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${from.email.address}")
  private String fromEmailAddress;

  @Value("${notification.email.enabled:true}")
  private boolean emailEnabled;

  @Override
  public boolean send(NotificationRequest request) {
    if (!isAvailable()) {
      log.warn("Email channel is not available");
      return false;
    }

    try {
      String emailContent = buildEmailContent(request);
      sendEmail(request.getRecipient().getEmail(), request.getSubject(), emailContent);

      log.debug("Email notification sent successfully to: {}",
          request.getRecipient().getEmail());
      return true;

    } catch (Exception e) {
      log.error("Failed to send email notification to: {}",
          request.getRecipient().getEmail(), e);
      return false;
    }
  }

  @Override
  public NotificationChannelType getChannelType() {
    return NotificationChannelType.EMAIL;
  }

  @Override
  public boolean isAvailable() {
    return emailEnabled && mailSender != null && fromEmailAddress != null;
  }

  private String buildEmailContent(NotificationRequest request) {
    if (request.getContent() == null) {
      throw new IllegalArgumentException(
          "Email content or template name is required for email notifications");
    }

    // If template variables are null or empty, treat content as plain text
    if (request.getTemplateVariables() == null || request.getTemplateVariables().isEmpty()) {
      return request.getContent();
    }

    // Otherwise, process as template
    Context context = new Context();
    for (Map.Entry<String, Object> entry : request.getTemplateVariables().entrySet()) {
      context.setVariable(entry.getKey(), entry.getValue());
    }

    return templateEngine.process(request.getContent(), context);
  }

  private void sendEmail(String recipient, String subject, String content)
      throws MessagingException, MailException {
    
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
    
    helper.setFrom(fromEmailAddress);
    helper.setTo(recipient);
    helper.setSubject(subject);
    helper.setText(content, true);
    
    mailSender.send(mimeMessage);
  }
}