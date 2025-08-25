package com.ratifire.devrate.service.notification.model;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.enums.NotificationType;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * Immutable notification request object that encapsulates all data needed
 * to send a notification through any channel.
 * Implements the Command pattern.
 */
@Data
@Builder
public class NotificationRequest {

  private final User recipient;
  private final NotificationType type;
  private final String subject;
  private final String content;
  private final Map<String, Object> templateVariables;
  private final Object payload;
  private final NotificationMetadata metadata;

  /**
   * Creates a notification request for WebSocket/Web Push notifications.
   */
  public static NotificationRequest forInAppNotification(User recipient,
      NotificationType type, Object payload, NotificationMetadata metadata) {
    return NotificationRequest.builder()
        .recipient(recipient)
        .type(type)
        .payload(payload)
        .metadata(metadata)
        .build();
  }

  /**
   * Creates a notification request for email notifications.
   */
  public static NotificationRequest forEmailNotification(User recipient,
      String subject, String templateName, Map<String, Object> templateVariables,
      NotificationMetadata metadata) {
    return NotificationRequest.builder()
        .recipient(recipient)
        .subject(subject)
        .content(templateName)
        .templateVariables(templateVariables)
        .metadata(metadata)
        .build();
  }

  /**
   * Creates a notification request with both in-app and email data.
   */
  public static NotificationRequest forMultiChannelNotification(User recipient,
      NotificationType type, Object payload, String subject, String templateName,
      Map<String, Object> templateVariables, NotificationMetadata metadata) {
    return NotificationRequest.builder()
        .recipient(recipient)
        .type(type)
        .payload(payload)
        .subject(subject)
        .content(templateName)
        .templateVariables(templateVariables)
        .metadata(metadata)
        .build();
  }
}