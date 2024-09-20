package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the notification.
 */
@Builder
@Getter
public class NotificationDto {

  private long id;
  private String payload;
  private NotificationType type;
  private boolean read;
}
