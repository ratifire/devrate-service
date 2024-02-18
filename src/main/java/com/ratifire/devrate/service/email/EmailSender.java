package com.ratifire.devrate.service.email;

/**
 * Interface for sending emails.
 *
 * <p>Implementations of this interface are responsible for sending emails to the specified
 * email address with the provided content.
 */
public interface EmailSender {

  /**
   * Sends an email with the specified content to the provided email address.
   *
   * @param subject   The email subject.
   * @param to   The email address to which the email should be sent.
   * @param text The message text.
   * @param html {@code true} the param 'text' will be html content
   *             {@code false} the param 'text' will be plain text content
   */
  void sendEmail(String subject, String to, String text, boolean html);
}
