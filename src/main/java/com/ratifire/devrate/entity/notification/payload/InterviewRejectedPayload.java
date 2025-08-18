package com.ratifire.devrate.entity.notification.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for notifications related to rejected interviews.
 *
 * @see NotificationPayload
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewRejectedPayload implements NotificationPayload  {

  @JsonProperty
  private String rejectionName;
  @JsonProperty
  private String scheduledDateTime;
  @JsonProperty
  private String rejectedInterviewId;
}
