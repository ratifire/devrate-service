package com.ratifire.devrate.dto;

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
  private boolean read;
}
