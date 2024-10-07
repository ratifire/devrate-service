package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.NotificationType;
import java.time.LocalDateTime;
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
  private LocalDateTime createdAt;
}
