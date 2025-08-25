package com.ratifire.devrate.service.notification.model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * Metadata associated with a notification request.
 */
@Data
@Builder
public class NotificationMetadata {

  private final Long interviewId;
  private final Long interviewRequestId;
  private final ZonedDateTime scheduledTime;
  private final String rejectionUserName;
  private final String role;
  private final LocalDateTime createdAt;
  private final Map<String, String> additionalData;
  private final boolean persistInDatabase;

  /**
   * Creates default notification metadata with current timestamp and database persistence enabled.
   *
   * @return NotificationMetadata with default settings
   */
  public static NotificationMetadata defaultMetadata() {
    return NotificationMetadata.builder()
        .createdAt(LocalDateTime.now())
        .persistInDatabase(true)
        .build();
  }

  /**
   * Creates notification metadata for interview-related notifications.
   *
   * @param interviewId The ID of the interview
   * @param scheduledTime The scheduled time of the interview
   * @param role The role of the recipient in the interview
   * @return NotificationMetadata configured for interview notifications
   */
  public static NotificationMetadata withInterview(Long interviewId, ZonedDateTime scheduledTime,
      String role) {
    return NotificationMetadata.builder()
        .interviewId(interviewId)
        .scheduledTime(scheduledTime)
        .role(role)
        .createdAt(LocalDateTime.now())
        .persistInDatabase(true)
        .build();
  }

  /**
   * Creates notification metadata for interview rejection notifications.
   *
   * @param rejectionUserName The name of the user who rejected the interview
   * @param scheduledTime The scheduled time of the rejected interview
   * @param interviewId The ID of the rejected interview
   * @return NotificationMetadata configured for rejection notifications
   */
  public static NotificationMetadata withRejection(String rejectionUserName,
      ZonedDateTime scheduledTime, Long interviewId) {
    return NotificationMetadata.builder()
        .rejectionUserName(rejectionUserName)
        .scheduledTime(scheduledTime)
        .interviewId(interviewId)
        .createdAt(LocalDateTime.now())
        .persistInDatabase(true)
        .build();
  }
}