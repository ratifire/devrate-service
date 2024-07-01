package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.NotificationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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

  private final NotificationRepository notificationRepository;
  private final UserSecurityService userSecurityService;
  private final DataMapper<NotificationDto, Notification> mapper;

  /**
   * Adds a notification for the given user with the specified text.
   *
   * @param text The text of the notification.
   * @param user The user to add the notification for.
   */
  public void addNotification(String text, User user) {
    Notification notification = Notification.builder()
        .text(text)
        .read(false)
        .createdAt(LocalDateTime.now())
        .user(user)
        .build();

    notificationRepository.save(notification);
  }

  /**
   * Adds a greeting notification for the given user.
   *
   * @param user The user to add the greeting notification for.
   */
  public void addGreetingNotification(User user) {
    String text = """
        Welcome aboard!
        We're thrilled to have you join DevRate community.
                
        At DevRate, we're all about empowering developers like you to share your expertise,
        learn from others, and build meaningful connections.
                
        Happy interviewing!
                
        Best regards,
        DevRate""";

    addNotification(text, user);
  }

  /**
   * Adds a notification to inform the user that their interview request has expired.
   *
   * @param user The user to whom the expiration notification will be sent.
   */
  public void addInterviewRequestExpiryNotification(User user) {
    String text = """
        Dear {userFirstName},

        Your interview request has expired.\s
        Please submit a new request if you still wish to proceed.

        Best regards,
        DevRate""";

    addNotification(text.replace("{userFirstName}", user.getFirstName()), user);
  }

  /**
   * Retrieves all notifications associated with a user's email.
   *
   * @param email The user's email.
   * @return A list of NotificationDto objects.
   */
  public List<NotificationDto> getAllByEmail(String email) {
    User user = userSecurityService.findByEmail(email).getUser();
    return mapper.toDto(user.getNotifications());
  }

  /**
   * Marks a notification as read by its ID.
   *
   * @param id The ID of the notification to mark as read.
   */
  public void markAsReadById(long id) {
    Notification notification = notificationRepository.findById(id)
        .orElseThrow(() -> new NotificationNotFoundException(id));

    notification.setRead(true);
    notificationRepository.save(notification);
  }

  /**
   * Deletes a notification by its ID.
   *
   * @param id The ID of the notification to delete.
   */
  public void deleteById(long id) {
    notificationRepository.deleteById(id);
  }

  /**
   * Add the notification for interview cancellation.
   *
   * @param recipientUser The user for whom the interview was rejected.
   * @param rejectionUserFirstName The first name of the user who rejected the interview.
   * @param scheduleTime The scheduled time of the interview.
   */
  public void addInterviewRejectNotification(User recipientUser,
      String rejectionUserFirstName, ZonedDateTime scheduleTime) {
    String text = String.format("""
        The interview with %s that was scheduled at %s has been canceled, but fret not!\s
        We will arrange another one soon and keep you informed promptly.""",
        rejectionUserFirstName, scheduleTime);

    addNotification(text, recipientUser);
  }
}
