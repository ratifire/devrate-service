package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.dto.WebPushSubscriptionDto;
import com.ratifire.devrate.service.WebPushNotificationService;
import com.ratifire.devrate.service.WebPushSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling web push subscriptions.
 */
@RestController
@RequestMapping("/web-subscription/")
@RequiredArgsConstructor
public class WebPushSubscriptionController {

  private final WebPushSubscriptionService subscriptionService;
  private final WebPushNotificationService notificationService;

  @PostMapping("/subscribe")
  public void subscribe(@RequestBody WebPushSubscriptionDto dto) {
    subscriptionService.saveSubscription(dto);
  }

  @PostMapping("/unsubscribe")
  public void unsubscribe(@RequestBody WebPushSubscriptionDto dto) {
    subscriptionService.deleteSubscription(dto.getEndpoint());
  }

  /**
   * Send test web push notification message.
   *
   * @param userId  logged user id
   * @param message message that will be sent
   * @return status code
   */
  @PostMapping("/test-message/send")
  public ResponseEntity<Void> sendTestNotification(@RequestParam Long userId,
      @RequestBody NotificationDto message) {
    try {
      notificationService.sendNotification(userId, message);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}
