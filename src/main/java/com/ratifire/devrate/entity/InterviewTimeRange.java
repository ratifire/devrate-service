package com.ratifire.devrate.entity;

import jakarta.persistence.Embeddable;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Embeddable class representing a time range for an interview.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class InterviewTimeRange {

  private OffsetDateTime startTime;
  private OffsetDateTime endTime;
}