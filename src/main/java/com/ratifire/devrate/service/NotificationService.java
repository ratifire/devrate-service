package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.NotificationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.NotificationRepository;
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
  public final DataMapper<NotificationDto, Notification> mapper;

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
   * Saves a notification.
   *
   * @param notification The notification to save.
   * @return The saved notification.
   */
  public Notification save(Notification notification) {
    return notificationRepository.save(notification);
  }
}
