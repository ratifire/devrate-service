package com.ratifire.devrate.dto;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object representing a user main mastery skills.
 */
@Getter
@Builder
@EqualsAndHashCode
public class UserMainMasterySkillDto {

  private SpecializationDto specialization;
  private MasteryDto mainMastery;
  private List<SkillDto> mainMasterySkills;
}
