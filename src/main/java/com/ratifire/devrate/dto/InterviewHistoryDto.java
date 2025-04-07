package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.InterviewRequestRole;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the InterviewHistory.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewHistoryDto {

  private long id;

  private ZonedDateTime dateTime;

  private long duration;

  private long userId;

  private Map<String, BigDecimal> softSkills;

  private Map<String, BigDecimal> hardSkills;

  private String specialization;

  private int masteryLevel;

  private InterviewRequestRole role;

  private int attendeeId;

  private String attendeeFirstName;

  private String attendeeLastName;

  private int attendeeMasteryLevel;

  private String attendeeSpecialization;

  private String feedback;

}
