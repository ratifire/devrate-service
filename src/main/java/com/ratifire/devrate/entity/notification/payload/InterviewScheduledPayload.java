package com.ratifire.devrate.entity.notification.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for notifications related to scheduled interviews.
 *
 * @see NotificationPayload
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewScheduledPayload implements NotificationPayload  {

  @JsonProperty
  private String role;
  @JsonProperty
  private String scheduledDateTime;
  @JsonProperty
  private long interviewId;
}
