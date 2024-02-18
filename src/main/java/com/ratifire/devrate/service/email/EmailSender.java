package com.ratifire.devrate.service.email;

import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;

/**
 * Interface for sending emails.
 *
 * <p>Implementations of this interface are responsible for sending emails to the specified
 * email address with the provided content.
 */
public interface EmailSender {

  /**
   * Sends an email using the provided {@link SimpleMailMessage}.
   *
   * @param simpleMailMessage The SimpleMailMessage containing email details such as
   *                          recipient, subject, and message body.
   * @param html              A boolean indicating whether the email content is HTML.
   *                          If true, the content is treated as HTML; otherwise, it is plain text.
   * @throws MailSendException If there is an issue sending the email.
   */
  void sendEmail(SimpleMailMessage simpleMailMessage, boolean html);
}
