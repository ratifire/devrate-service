package com.ratifire.devrate.util.zoom.webhook.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Represents the payload for a Zoom event webhook.
 */
@Data
@Builder
public class WebHookRequest {
  private String event;
  @JsonProperty("event_ts")
  private long eventTs;
  private Payload payload;

  /**
   * Represents the payload details for the Zoom event.
   */
  @Data
  @Builder
  public static class Payload {
    @JsonProperty("account_id")
    private String accountId;
    @JsonProperty("object")
    private Meeting meeting;
    @JsonProperty("plain_token")
    private String plainToken;

    /**
     * Represents the meeting object details in the payload.
     */
    @Data
    @Builder
    public static class Meeting {
      private String id; // Meeting ID
      @JsonProperty("end_time")
      private String endTime; // End time of the meeting
    }
  }
}