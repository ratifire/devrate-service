package com.ratifire.devrate.service;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Service implementation for sending emails using JavaMailSender.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${from.email.address}")
  private String fromEmailAddress;

  /**
   * Sends a welcome email to the user.
   *
   * @param user  The user object containing user information.
   */
  public void sendGreetingsEmail(User user) {
    String subject = "Welcome to DevRate!";
    String text = buildTemplateEmailText("greeting-email", Map.of("user", user));
    sendEmail(user.getEmail(), subject, text);
  }

  /**
   * Sends an email confirmation about the password change.
   *
   * @param email The user's email address.
   */
  public void sendPasswordChangeConfirmationEmail(String email) {
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
   * Sends an email to notify recipient about interview rejection.
   *
   * @param recipientUser The user for whom the interview was rejected.
   * @param rejectionUser The user who rejected the interview.
   * @param scheduledTime The scheduled time of the interview.
   */
  public void sendInterviewRejectionMessageEmail(User recipientUser, User rejectionUser,
      ZonedDateTime scheduledTime, String email) {
    Map<String, Object> model = new HashMap<>();
    model.put("recipientUser", recipientUser);
    model.put("rejectionUser", rejectionUser);
    model.put("scheduledTime", scheduledTime.withZoneSameInstant(ZoneId.of("Europe/Kyiv")));

    String subject = "Interview Rejected";
    String text = buildTemplateEmailText("interview-rejected-email", model);
    sendEmail(email, subject, text);
  }

  /**
   * Sends an email to notify the recipient about the scheduled interview.
   *
   * @param recipient         the user who will receive the email
   * @param email             the email address of the recipient
   * @param interviewDateTime the date and time of the interview
   * @param interviewRequest  the interview request containing details about the interview
   * @param interviewId       the interview id
   */
  public void sendInterviewScheduledEmail(User recipient, String email,
      ZonedDateTime interviewDateTime, InterviewRequest interviewRequest, long interviewId) {

    Map<String, Object> model = new HashMap<>();
    model.put("recipient", recipient);
    model.put("interviewDateTime", interviewDateTime.withZoneSameInstant(ZoneId.of("Europe/Kyiv")));
    model.put("interviewRequest", interviewRequest);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    String googleCalendarUrl = "https://www.google.com/calendar/render?action=TEMPLATE"
        + "&text=" + URLEncoder.encode("Інтерв’ю Skillzzy", StandardCharsets.UTF_8)
        + "&dates=" + formatter.format(interviewDateTime) + "/" + formatter.format(
        interviewDateTime.plusMinutes(60));

    model.put("googleCalendarUrl", googleCalendarUrl);

    String template =
        interviewRequest.getRole().equals(InterviewRequestRole.CANDIDATE)
            ? "interviewer-interview-scheduled-email"
            : "candidate-interview-scheduled-email";
    String text = buildTemplateEmailText(template, model);
    String subject = "Interview Scheduled Successfully";
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
      helper.setFrom(fromEmailAddress);
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

}
