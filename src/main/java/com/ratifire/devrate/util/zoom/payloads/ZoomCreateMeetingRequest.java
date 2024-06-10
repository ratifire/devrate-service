package com.ratifire.devrate.util.zoom.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Request object for creating a meeting in Zoom.
 */
@Builder
@Data
public class ZoomCreateMeetingRequest {

  private String agenda;
  private int duration;
  private Settings settings;
  @JsonProperty("start_time")
  private String startTime;
  private String timezone;
  private String topic;
  private int type;

  /**
   * Settings for the request meeting.
   */
  @Builder
  @Data
  public static class Settings {
    @JsonProperty("jbh_time")
    private int jbhTime;
    @JsonProperty("join_before_host")
    private boolean joinBeforeHost;
    @JsonProperty("meeting_authentication")
    private boolean meetingAuthentication;
    @JsonProperty("private_meeting")
    private boolean privateMeeting;
  }
}
