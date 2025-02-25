package com.ratifire.devrate.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the count of conducted
 * and passed interviews on a specific date.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewStatsConductedPassedByDateDto {
  private ZonedDateTime date;
  private long conducted;
  private long passed;

}