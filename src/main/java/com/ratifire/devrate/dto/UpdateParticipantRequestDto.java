package com.ratifire.devrate.dto;

import java.util.Date;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object representing the update participant interview request data.
 */
@Setter
@Getter
@Builder
public class UpdateParticipantRequestDto {
  private int desiredInterview;
  private int matchedInterview;
  private Set<Date> dates;
}
