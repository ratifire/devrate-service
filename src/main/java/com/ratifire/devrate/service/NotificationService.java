package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.entity.notification.payload.InterviewFeedbackPayload;
import com.ratifire.devrate.entity.notification.payload.InterviewRejectedPayload;
import com.ratifire.devrate.entity.notification.payload.InterviewRequestExpiredPayload;
import com.ratifire.devrate.entity.notification.payload.InterviewScheduledPayload;
import com.ratifire.devrate.entity.notification.payload.NotificationPayload;
import com.ratifire.devrate.enums.NotificationType;
import com.ratifire.devrate.exception.NotificationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.NotificationRepository;
import com.ratifire.devrate.util.converter.JsonConverter;
import com.ratifire.devrate.util.websocket.WebSocketSender;
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

  private final NotificationRepository notificationRepository;
  private final UserSecurityService userSecurityService;
  private final DataMapper<NotificationDto, Notification> mapper;
  private final WebSocketSender webSocketSender;

  /**
   * Adds a greeting notification for the given user.
   *
   * @param user The user to add the greeting notification for.
   */
  public void addGreetingNotification(User user) {
    Notification notification = createNotification(NotificationType.GREETING, null, user);

    addNotification(notification, user);
  }

  /**
   * Adds a notification to inform the user that their interview request has expired.
   *
   * @param user The user to whom the expiration notification will be sent.
   */
  public void addInterviewRequestExpiryNotification(User user) {
    InterviewRequestExpiredPayload expiredPayload = InterviewRequestExpiredPayload.builder()
        .userFirstName(user.getFirstName())
        .build();

    Notification notification = createNotification(NotificationType.INTERVIEW_REQUEST_EXPIRED,
        expiredPayload, user);

    addNotification(notification, user);
  }

  /**
   * Add the notification for the rejected interview.
   *
   * @param recipient The user for whom the interview was rejected.
   * @param rejectionUserFirstName The first name of the user who rejected the interview.
   * @param scheduleTime The scheduled time of the interview.
   */
  public void addRejectInterview(User recipient,
      String rejectionUserFirstName, ZonedDateTime scheduleTime) {
    InterviewRejectedPayload rejectedPayload = InterviewRejectedPayload.builder()
        .rejectionName(rejectionUserFirstName)
        .scheduledDateTime(scheduleTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
        .build();

    Notification notification = createNotification(NotificationType.INTERVIEW_REJECTED,
        rejectedPayload, recipient);

    addNotification(notification, recipient);
  }

  /**
   * Adds a notification to inform the user that their interview has been successfully scheduled.
   *
   * @param recipient         The user to whom the notification will be sent.
   * @param interviewDateTime The date and time of the scheduled interview.
   * @param role              The role of the recipient in the interview (e.g., 'Interviewer' or
   *                          'Candidate').
   */
  public void addInterviewScheduled(User recipient, String role, ZonedDateTime interviewDateTime) {
    InterviewScheduledPayload scheduledPayload = InterviewScheduledPayload.builder()
        .role(role)
        .scheduledDateTime(
            interviewDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
        .build();

    Notification notification = createNotification(NotificationType.INTERVIEW_SCHEDULED,
        scheduledPayload, recipient);

    addNotification(notification, recipient);
  }

  /**
   * Adds an interview feedback notification for a specified user.
   *
   * @param recipient  The user who will receive the notification.
   * @param feedbackId The ID of the feedback to include in the notification payload.
   */
  public void addInterviewFeedbackDetail(User recipient, long feedbackId) {
    InterviewFeedbackPayload feedbackPayload = InterviewFeedbackPayload.builder()
        .feedbackId(feedbackId)
        .build();

    Notification notification = createNotification(NotificationType.INTERVIEW_FEEDBACK,
        feedbackPayload, recipient);

    addNotification(notification, recipient);
  }

  /**
   * Creates a notification with the given type, payload, and user.
   *
   * @param type    The type of notification.
   * @param payload The payload to be serialized and added to the notification.
   * @param user    The user to whom the notification will be associated.
   * @return The built Notification object.
   */
  private Notification createNotification(NotificationType type, NotificationPayload payload,
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
   * @param user         The user to add the notification for.
   */
  private void addNotification(Notification notification, User user) {
    notificationRepository.save(notification);

    sendUserNotifications(user.getId());
  }

  /**
   * Deletes a notification by its ID and send updated notifications.
   *
   * @param userId         The ID of the user associated with the notification.
   * @param notificationId The ID of the notification to delete.
   */
  public void deleteById(long userId, long notificationId) {
    notificationRepository.deleteById(notificationId);

    sendUserNotifications(userId);
  }

  /**
   * Sends the notifications to the user via WebSocket.
   *
   * @param userId The ID of the user to send updated notifications for.
   */
  private void sendUserNotifications(long userId) {
    UserSecurity userSecurity = userSecurityService.getByUserId(userId);
    String email = userSecurity.getEmail();
    List<NotificationDto> notifications = getAllByEmail(email);
    webSocketSender.sendNotificationsByUserEmail(notifications, email);
  }

  /**
   * Retrieves all notifications associated with a user's email.
   *
   * @param email The user's email.
   * @return A list of NotificationDto objects.
   */
  public List<NotificationDto> getAllByEmail(String email) {
    User user = userSecurityService.findByEmail(email).getUser();

    // we could get notifications from user, but during the LAZY mode we can not retrieve it regard
    // to closed DB session
    // TODO: should be refactored to reduce DB invocations
    List<Notification> notifications = notificationRepository.findNotificationsByUserId(
        user.getId()).orElseThrow(() -> new NotificationNotFoundException(
        "Can not find notifications by user id " + user.getId()));

    return mapper.toDto(notifications);
  }

  /**
   * Marks a notification as read by its ID and send updated notifications.
   *
   * @param userId         The ID of the user associated with the notification.
   * @param notificationId The ID of the notification to mark as read.
   */
  public void markAsReadById(long userId, long notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException(notificationId));

    notification.setRead(true);
    notificationRepository.save(notification);

    sendUserNotifications(userId);
  }
}
