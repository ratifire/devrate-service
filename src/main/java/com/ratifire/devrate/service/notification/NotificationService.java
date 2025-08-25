package com.ratifire.devrate.service.notification;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.entity.notification.payload.InterviewRejectedPayload;
import com.ratifire.devrate.entity.notification.payload.InterviewRequestExpiredPayload;
import com.ratifire.devrate.entity.notification.payload.InterviewScheduledPayload;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.enums.NotificationType;
import com.ratifire.devrate.exception.NotificationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.NotificationRepository;
import com.ratifire.devrate.service.notification.factory.NotificationChannelFactory;
import com.ratifire.devrate.service.notification.model.NotificationMetadata;
import com.ratifire.devrate.service.notification.model.NotificationRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Unified notification service that acts as a facade for all notification operations.
 * Implements the Facade pattern to provide a simple interface for complex notification logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSS";

  private final NotificationChannelFactory channelFactory;
  private final NotificationRepository notificationRepository;
  private final DataMapper<NotificationDto, Notification> notificationMapper;

  /**
   * Sends a greeting notification to a new user via multiple channels.
   *
   * @param user The user to send greeting to
   */
  public void sendGreeting(User user) {
    // In-app notification
    NotificationRequest inAppRequest = NotificationRequest.forInAppNotification(
        user, NotificationType.GREETING, null, NotificationMetadata.defaultMetadata());

    sendNotification(inAppRequest, NotificationChannelType.WEBSOCKET,
        NotificationChannelType.WEB_PUSH);

    // Email notification
    NotificationRequest emailRequest = NotificationRequest.forEmailNotification(
        user, "Welcome to DevRate!", "greeting-email",
        Map.of("user", user), NotificationMetadata.defaultMetadata());

    sendNotification(emailRequest, NotificationChannelType.EMAIL);
  }

  /**
   * Sends an interview request expiration notification.
   *
   * @param user The user whose interview request expired
   */
  public void sendInterviewRequestExpiry(User user) {
    InterviewRequestExpiredPayload payload = InterviewRequestExpiredPayload.builder()
        .userFirstName(user.getFirstName())
        .build();

    // In-app notification
    NotificationRequest inAppRequest = NotificationRequest.forInAppNotification(
        user, NotificationType.INTERVIEW_REQUEST_EXPIRED, payload,
        NotificationMetadata.defaultMetadata());

    sendNotification(inAppRequest, NotificationChannelType.WEBSOCKET,
        NotificationChannelType.WEB_PUSH);

    // Email notification
    NotificationRequest emailRequest = NotificationRequest.forEmailNotification(
        user, "Interview Request Expired", "interview-request-expired-email",
        Map.of("user", user), NotificationMetadata.defaultMetadata());

    sendNotification(emailRequest, NotificationChannelType.EMAIL);
  }

  /**
   * Sends an interview rejection notification.
   *
   * @param recipient The user who will receive the rejection notification
   * @param rejectionUser The user who rejected the interview
   * @param scheduledTime The scheduled time of the rejected interview
   * @param recipientInterviewId The interview ID for the recipient
   */
  public void sendInterviewRejection(User recipient, User rejectionUser,
      ZonedDateTime scheduledTime, long recipientInterviewId) {
    
    NotificationMetadata metadata = NotificationMetadata.withRejection(
        rejectionUser.getFirstName(), scheduledTime, recipientInterviewId);

    InterviewRejectedPayload payload = InterviewRejectedPayload.builder()
        .rejectionName(rejectionUser.getFirstName())
        .scheduledDateTime(
            scheduledTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
        .rejectedInterviewId(String.valueOf(recipientInterviewId))
        .build();

    // In-app notification
    NotificationRequest inAppRequest = NotificationRequest.forInAppNotification(
        recipient, NotificationType.INTERVIEW_REJECTED, payload, metadata);

    sendNotification(inAppRequest, NotificationChannelType.WEBSOCKET,
        NotificationChannelType.WEB_PUSH);

    // Email notification
    Map<String, Object> emailVariables = new HashMap<>();
    emailVariables.put("recipientUser", recipient);
    emailVariables.put("rejectionUser", rejectionUser);
    emailVariables.put("scheduledTime",
        scheduledTime.withZoneSameInstant(ZoneId.of("Europe/Kyiv")));

    NotificationRequest emailRequest = NotificationRequest.forEmailNotification(
        recipient, "Interview Rejected", "interview-rejected-email",
        emailVariables, metadata);

    sendNotification(emailRequest, NotificationChannelType.EMAIL);
  }

  /**
   * Sends an interview scheduled notification.
   *
   * @param recipient The user who will receive the notification
   * @param role The role of the recipient in the interview
   * @param interviewDateTime The scheduled interview date and time
   * @param interviewRequest The interview request details
   * @param interviewId The interview ID
   */
  public void sendInterviewScheduled(User recipient, String role,
      ZonedDateTime interviewDateTime, InterviewRequest interviewRequest, long interviewId) {
    
    NotificationMetadata metadata = NotificationMetadata.withInterview(
        interviewId, interviewDateTime, role);

    InterviewScheduledPayload payload = InterviewScheduledPayload.builder()
        .role(role)
        .scheduledDateTime(
            interviewDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
        .interviewId(interviewId)
        .build();

    // In-app notification
    NotificationRequest inAppRequest = NotificationRequest.forInAppNotification(
        recipient, NotificationType.INTERVIEW_SCHEDULED, payload, metadata);

    sendNotification(inAppRequest, NotificationChannelType.WEBSOCKET,
        NotificationChannelType.WEB_PUSH);

    // Email notification
    Map<String, Object> emailVariables = createInterviewScheduledEmailVariables(
        recipient, interviewDateTime, interviewRequest);

    String templateName = interviewRequest.getRole().equals(InterviewRequestRole.CANDIDATE)
        ? "interviewer-interview-scheduled-email"
        : "candidate-interview-scheduled-email";

    NotificationRequest emailRequest = NotificationRequest.forEmailNotification(
        recipient, "Interview Scheduled Successfully", templateName,
        emailVariables, metadata);

    sendNotification(emailRequest, NotificationChannelType.EMAIL);
  }

  /**
   * Sends a notification through specified channels.
   *
   * @param request The notification request
   * @param channelTypes The channels to send through
   */
  public void sendNotification(NotificationRequest request,
      NotificationChannelType... channelTypes) {
    List<NotificationChannel> channels = channelFactory.getChannels(List.of(channelTypes));

    if (channels.isEmpty()) {
      log.warn("No available channels found for notification to user: {}",
          request.getRecipient().getId());
      return;
    }

    for (NotificationChannel channel : channels) {
      try {
        boolean success = channel.send(request);
        if (success) {
          log.debug("Notification sent successfully via {}", channel.getChannelType());
        } else {
          log.warn("Failed to send notification via {}", channel.getChannelType());
        }
      } catch (Exception e) {
        log.error("Error sending notification via {}", channel.getChannelType(), e);
      }
    }
  }

  /**
   * Retrieves all notifications for a user.
   *
   * @param userId The user ID
   * @return List of notification DTOs
   */
  public List<NotificationDto> getAllByUserId(long userId) {
    List<Notification> notifications = notificationRepository
        .findNotificationsByUserIdOrderByCreatedAtDesc(userId)
        .orElseThrow(() -> new NotificationNotFoundException(
            "Can not find notifications by user id " + userId));

    return notificationMapper.toDto(notifications);
  }

  /**
   * Marks a notification as read.
   *
   * @param notificationId The notification ID
   */
  public void markAsReadById(long notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException(notificationId));

    notification.setRead(true);
    notificationRepository.save(notification);
  }

  /**
   * Deletes a notification.
   *
   * @param notificationId The notification ID
   */
  public void deleteById(long notificationId) {
    notificationRepository.deleteById(notificationId);
  }

  /**
   * Sends a password change confirmation notification via email.
   *
   * @param email The user's email address
   */
  public void sendPasswordChangeConfirmation(String email) {
    // Create a minimal user object for email sending
    User emailUser = User.builder()
        .email(email)
        .build();

    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = now.format(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String content = "Your password has been successfully changed on "
        + formattedDateTime + ".";

    NotificationRequest emailRequest = NotificationRequest.builder()
        .recipient(emailUser)
        .subject("Password Successfully Reset")
        .content(content) // Plain text content instead of template
        .metadata(NotificationMetadata.builder()
            .createdAt(now)
            .persistInDatabase(false)
            .build())
        .build();

    sendNotification(emailRequest, NotificationChannelType.EMAIL);
  }

  private Map<String, Object> createInterviewScheduledEmailVariables(User recipient,
      ZonedDateTime interviewDateTime, InterviewRequest interviewRequest) {

    Map<String, Object> variables = new HashMap<>();
    variables.put("recipient", recipient);
    variables.put("interviewDateTime",
        interviewDateTime.withZoneSameInstant(ZoneId.of("Europe/Kyiv")));
    variables.put("interviewRequest", interviewRequest);

    // Google Calendar URL
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    String googleCalendarUrl = "https://www.google.com/calendar/render?action=TEMPLATE"
        + "&text=" + URLEncoder.encode("Інтерв'ю Skillzzy", StandardCharsets.UTF_8)
        + "&dates=" + formatter.format(interviewDateTime) + "/"
        + formatter.format(interviewDateTime.plusMinutes(60));

    variables.put("googleCalendarUrl", googleCalendarUrl);

    return variables;
  }
}