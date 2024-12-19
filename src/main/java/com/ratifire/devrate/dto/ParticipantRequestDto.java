package com.ratifire.devrate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ratifire.devrate.enums.InterviewRequestRole;
import java.util.Date;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object representing the participant interview request data.
 */
@Setter
@Getter
@Builder
public class ParticipantRequestDto {

  private long participantId;
  private int desiredInterview;
  @JsonProperty("type")
  private InterviewRequestRole role;
  private String specialization;
  private int masteryLevel;
  private Set<String> hardSkills;
  private Set<String> softSkills;
  private Set<Date> dates;
  private double averageMark;    // need to be improved
  private Set<Integer> blackList;
}
