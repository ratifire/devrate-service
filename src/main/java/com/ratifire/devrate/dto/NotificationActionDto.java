package com.ratifire.devrate.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the action over notification.
 */
@Builder
@Getter
public class NotificationActionDto {

  private long notificationId;
  private String action;
}
