package com.ratifire.devrate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing response for created Zoom meeting.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ZoomCreateMeetingDto {

  /**
   * Meeting url for candidate - to be a participant.
   */
  private String joinUrl;

  /**
   * Meeting url for interviewer - to be a host.
   */
  private String startUrl;
}
