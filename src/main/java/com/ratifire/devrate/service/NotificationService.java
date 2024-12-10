package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.notification.payload.InterviewFeedbackPayload;
import com.ratifire.devrate.entity.notification.payload.InterviewRejectedPayload;
import com.ratifire.devrate.entity.notification.payload.InterviewRequestExpiredPayload;
import com.ratifire.devrate.entity.notification.payload.InterviewScheduledPayload;
import com.ratifire.devrate.entity.notification.payload.NotificationPayload;
import com.ratifire.devrate.enums.NotificationType;
import com.ratifire.devrate.exception.NotificationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.NotificationRepository;
import com.ratifire.devrate.util.JsonConverter;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing notifications. This class provides methods for retrieving, reading,
 * and deleting notifications.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

  private final NotificationRepository repository;
  private final DataMapper<NotificationDto, Notification> mapper;
  private final WebSocketSender webSocketSender;

  /**
   * Adds a greeting notification for the given user.
   *
   * @param user      The user to add the greeting notification for.
   * @param userEmail The user email to add the notification for.
   */
  public void addGreeting(User user, String userEmail) {
    Notification notification = create(NotificationType.GREETING, null, user);

    save(notification, userEmail);
  }

  /**
   * Adds a notification to inform the user that their interview request has expired.
   *
   * @param user      The user to whom the expiration notification will be sent.
   * @param userEmail The user email to add the notification for.
   */
  public void addInterviewRequestExpiry(User user, String userEmail) {
    InterviewRequestExpiredPayload expiredPayload = InterviewRequestExpiredPayload.builder()
        .userFirstName(user.getFirstName())
        .build();

    Notification notification = create(NotificationType.INTERVIEW_REQUEST_EXPIRED,
        expiredPayload, user);

    save(notification, userEmail);
  }

  /**
   * Add the notification for the rejected interview.
   *
   * @param recipient              The user for whom the interview was rejected.
   * @param rejectionUserFirstName The first name of the user who rejected the interview.
   * @param scheduleTime           The scheduled time of the interview.
   * @param userEmail              The user email to add the notification for.
   */
  public void addRejectInterview(User recipient,
      String rejectionUserFirstName, ZonedDateTime scheduleTime, String userEmail) {
    InterviewRejectedPayload rejectedPayload = InterviewRejectedPayload.builder()
        .rejectionName(rejectionUserFirstName)
        .scheduledDateTime(scheduleTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
        .build();

    Notification notification = create(NotificationType.INTERVIEW_REJECTED,
        rejectedPayload, recipient);

    save(notification, userEmail);
  }

  /**
   * Adds a notification to inform the user that their interview has been successfully scheduled.
   *
   * @param recipient         The user to whom the notification will be sent.
   * @param interviewDateTime The date and time of the scheduled interview.
   * @param role              The role of the recipient in the interview (e.g., 'Interviewer' or
   *                          'Candidate').
   * @param userEmail         The user email to add the notification for.
   */
  public void addInterviewScheduled(User recipient, String role, ZonedDateTime interviewDateTime,
      String userEmail) {
    InterviewScheduledPayload scheduledPayload = InterviewScheduledPayload.builder()
        .role(role)
        .scheduledDateTime(
            interviewDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
        .build();

    Notification notification = create(NotificationType.INTERVIEW_SCHEDULED,
        scheduledPayload, recipient);

    save(notification, userEmail);
  }

  /**
   * Adds an interview feedback notification for a specified user.
   *
   * @param recipient  The user who will receive the notification.
   * @param feedbackId The ID of the feedback to include in the notification payload.
   * @param userEmail  The user email to add the notification for.
   */
  public void addInterviewFeedbackDetail(User recipient, long feedbackId, String userEmail) {
    InterviewFeedbackPayload feedbackPayload = InterviewFeedbackPayload.builder()
        .feedbackId(feedbackId)
        .build();

    Notification notification = create(NotificationType.INTERVIEW_FEEDBACK,
        feedbackPayload, recipient);

    save(notification, userEmail);
  }

  /**
   * Creates a notification with the given type, payload, and user.
   *
   * @param type    The type of notification.
   * @param payload The payload to be serialized and added to the notification.
   * @param user    The user to whom the notification will be associated.
   * @return The built Notification object.
   */
  private Notification create(NotificationType type, NotificationPayload payload,
      User user) {
    return Notification.builder()
        .type(type)
        .payload(payload != null ? JsonConverter.serialize(payload) : null)
        .read(false)
        .createdAt(LocalDateTime.now())
        .user(user)
        .build();
  }

  /**
   * Adds a notification for the given user with the specified text and send updated notifications.
   *
   * @param notification The notification that will be saved.
   * @param userEmail    The user email to add the notification for.
   */
  private void save(Notification notification, String userEmail) {
    repository.save(notification);

    sendToUser(userEmail, mapper.toDto(notification));
  }

  /**
   * Deletes a notification by its ID and send updated notifications.
   *
   * @param notificationId The ID of the notification to delete.
   */
  public void delete(long notificationId) {
    repository.deleteById(notificationId);
  }

  /**
   * Sends a notification to the specified user via WebSocket.
   *
   * @param userEmail    The email of the user to send fresh notification for.
   * @param notification The notification data to be sent.
   */
  private void sendToUser(String userEmail, NotificationDto notification) {
    webSocketSender.sendNotificationByUserEmail(notification, userEmail);
  }

  /**
   * Retrieves all notifications associated with a user's id.
   *
   * @param userId The user's id.
   * @return A list of NotificationDto objects.
   */
  public List<NotificationDto> getAllByUserId(long userId) {
    // we could get notifications from user, but during the LAZY mode we can not retrieve it regard
    // to closed DB session
    // TODO: should be refactored to reduce DB invocations
    List<Notification> notifications = repository.findNotificationsByUserId(userId)
        .orElseThrow(() -> new NotificationNotFoundException(
            "Can not find notifications by user id " + userId));

    return mapper.toDto(notifications);
  }

  /**
   * Marks a notification as read by its ID and send updated notifications.
   *
   * @param notificationId The ID of the notification to mark as read.
   */
  public void markAsReadById(long notificationId) {
    Notification notification = repository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException(notificationId));

    notification.setRead(true);
    repository.save(notification);
  }

  /**
   * Sends a test notification to a specified user.
   */
  // TODO: ATTENTION!!! Remove this method after testing is completed.
  public void sendTestNotification(String userEmail, Notification notification) {
    save(notification, userEmail);
  }
}
