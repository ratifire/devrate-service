package com.ratifire.devrate.entity.notification.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for notifications related to interview feedback.
 *
 * @see NotificationPayload
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewFeedbackPayload implements NotificationPayload {

  @JsonProperty
  private Long feedbackId;
}
