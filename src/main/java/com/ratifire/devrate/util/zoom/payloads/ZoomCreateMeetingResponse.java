package com.ratifire.devrate.util.zoom.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response object for creating a meeting in Zoom.
 */
@Data
public class ZoomCreateMeetingResponse {

  public long id;
  @JsonProperty("join_url")
  public String joinUrl;
}
