package com.ratifire.devrate.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the interview summary.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSummaryDto {

  private long id;
  private LocalDate date;
  private long duration;
  private long candidateId;
  private long interviewerId;

}
