package com.ratifire.devrate.controller;

import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.util.websocket.WebSocketSender;
import lombok.RequiredArgsConstructor;
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
  private final UserSecurityService userSecurityService;
  private final WebSocketSender webSocketSender;

  /**
   * Marks a notification as read.
   *
   * @param userId         The ID of the user associated with the notification.
   * @param notificationId The ID of the notification to be marked as read.
   */
  @PatchMapping
  public void read(@RequestParam long userId, @RequestParam long notificationId) {
    notificationService.markAsReadById(notificationId);
    UserSecurity userSecurity = userSecurityService.getByUserId(userId);
    webSocketSender.sendNotificationsByUserEmail(userSecurity.getEmail());
  }

  /**
   * Deletes a notification.
   *
   * @param userId         The ID of the user associated with the notification.
   * @param notificationId The ID of the notification to be deleted.
   */
  @DeleteMapping
  public void delete(@RequestParam long userId, @RequestParam long notificationId) {
    notificationService.deleteById(notificationId);
    UserSecurity userSecurity = userSecurityService.getByUserId(userId);
    webSocketSender.sendNotificationsByUserEmail(userSecurity.getEmail());
  }
}
