package com.ratifire.devrate.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the notification.
 */
@Builder
@Getter
public class NotificationDto {

  private long id;
  @Size(max = 100)
  private String text;
  private boolean read;
}
