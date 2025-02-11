package com.ratifire.devrate.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO class for transferring Skill set information.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SkillSetDto {

  private List<SkillDto> hardSkills;
  private List<SkillDto> softSkills;
}