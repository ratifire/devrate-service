package com.ratifire.devrate.service.notification.channel;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.service.WebPushNotificationService;
import com.ratifire.devrate.service.notification.NotificationChannel;
import com.ratifire.devrate.service.notification.NotificationChannelType;
import com.ratifire.devrate.service.notification.model.NotificationRequest;
import com.ratifire.devrate.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Web Push notification channel implementation.
 * Handles sending push notifications to web browsers.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebPushNotificationChannel implements NotificationChannel {

  private final WebPushNotificationService webPushNotificationService;

  @Value("${notification.web-push.enabled:true}")
  private boolean webPushEnabled;

  @Override
  public boolean send(NotificationRequest request) {
    if (!isAvailable()) {
      log.warn("Web Push channel is not available");
      return false;
    }

    try {
      NotificationDto notificationDto = createNotificationDto(request);
      webPushNotificationService.sendNotification(request.getRecipient().getId(), notificationDto);

      return true;
    } catch (Exception e) {
      log.error("Failed to send Web Push notification to user: {}",
          request.getRecipient().getId(), e);
      return false;
    }
  }

  @Override
  public NotificationChannelType getChannelType() {
    return NotificationChannelType.WEB_PUSH;
  }

  @Override
  public boolean isAvailable() {
    return webPushEnabled && webPushNotificationService != null;
  }

  private NotificationDto createNotificationDto(NotificationRequest request) {
    return NotificationDto.builder()
        .type(request.getType())
        .payload(request.getPayload() != null
            ? JsonConverter.serialize(request.getPayload()) : null)
        .read(false)
        .createdAt(request.getMetadata().getCreatedAt())
        .build();
  }
}