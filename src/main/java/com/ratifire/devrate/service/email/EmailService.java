package com.ratifire.devrate.service.email;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Service implementation for sending emails using JavaMailSender.
 */
@AllArgsConstructor
@Service
public class EmailService {

  private static final Logger log = LogManager.getLogger(EmailService.class);

  private JavaMailSender mailSender;

  /**
   * Sends an email using the provided {@link SimpleMailMessage}.
   * This method creates a MimeMessage and uses MimeMessageHelper to set the email's text,
   * recipient, and subject. The email is sent using the configured mail sender.
   *
   * @param simpleMailMessage The SimpleMailMessage containing email details.
   * @param isHtmlFormat              A boolean indicating whether the email content is HTML.
   * @throws IllegalArgumentException If any of the required parameters in SimpleMailMessage are
   *                                  null.
   * @throws MailSendException    If an exception occurs and the email cannot be sent.
   * @see SimpleMailMessage
   * @see MimeMessage
   * @see MimeMessageHelper
   */
  public void sendEmail(SimpleMailMessage simpleMailMessage, boolean isHtmlFormat) {
    try {
      validateSimpleMailMessage(simpleMailMessage);

      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
      helper.setText(simpleMailMessage.getText(), isHtmlFormat);
      helper.setTo(simpleMailMessage.getTo());
      helper.setSubject(simpleMailMessage.getSubject());

      mailSender.send(mimeMessage);

      log.info("The email sent successfully to '{}'. Subject: '{}'",
          formatEmailAddresses(simpleMailMessage.getTo()), simpleMailMessage.getSubject());
    } catch (Exception ex) {
      throw new MailSendException("Failed to send the email to '"
          + formatEmailAddresses(simpleMailMessage.getTo()) + "'."
          + " Subject: '" + simpleMailMessage.getSubject() + "'");
    }
  }

  private void validateSimpleMailMessage(SimpleMailMessage simpleMailMessage) {
    if (simpleMailMessage == null) {
      throw new IllegalArgumentException("The argument must not be null");
    }

    Assert.notNull(simpleMailMessage.getText(), "The text must not be null");
    Assert.notNull(simpleMailMessage.getTo(), "To address array must not be null");
    Assert.notNull(simpleMailMessage.getSubject(), "Subject must not be null");
  }

  private String formatEmailAddresses(String[] emailAddresses) {
    return StringUtils.arrayToCommaDelimitedString(emailAddresses);
  }
}
