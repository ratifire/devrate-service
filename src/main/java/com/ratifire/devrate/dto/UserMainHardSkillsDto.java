package com.ratifire.devrate.dto;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object representing a user main hard skills.
 */
@Getter
@Builder
@EqualsAndHashCode
public class UserMainHardSkillsDto {

  private String specializationName;
  private boolean isMainSpecialization;
  private String masteryName;
  private List<SkillDto> hardSkills;
}
