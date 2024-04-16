package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.NotificationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.NotificationRepository;
import com.ratifire.devrate.service.user.UserService;
import java.time.LocalDateTime;
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
  private final UserService userService;
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
        .build();

    user.getNotifications().add(notification);
    userService.updateUser(user);
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
}
