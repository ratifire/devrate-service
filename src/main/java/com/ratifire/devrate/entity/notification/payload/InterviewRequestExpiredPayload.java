package com.ratifire.devrate.entity.notification.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for notifications related to expired interview requests.
 *
 * @see NotificationPayload
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewRequestExpiredPayload implements NotificationPayload  {

  @JsonProperty
  private String userFirstName;
}
