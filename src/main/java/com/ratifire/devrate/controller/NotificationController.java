package com.ratifire.devrate.controller;

import com.ratifire.devrate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling notification-related endpoints.
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  /**
   * Marks a notification as read.
   *
   * @param userId         The ID of the user associated with the notification.
   * @param notificationId The ID of the notification to be marked as read.
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId) && "
      + "@resourceAuthorizationService.isResourceOwnedByLoggedUser('notifications' ,"
      + " #notificationId)")
  @PatchMapping
  public void markAsRead(@RequestParam long userId, @RequestParam long notificationId) {
    notificationService.markAsReadById(notificationId);
  }

  /**
   * Deletes a notification.
   *
   * @param userId         The ID of the user associated with the notification.
   * @param notificationId The ID of the notification to be deleted.
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId) && "
      + "@resourceAuthorizationService.isResourceOwnedByLoggedUser('notifications' ,"
      + " #notificationId)")
  @DeleteMapping
  public void delete(@RequestParam long userId, @RequestParam long notificationId) {
    notificationService.deleteById(notificationId);
  }
}
