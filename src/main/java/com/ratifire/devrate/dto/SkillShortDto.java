package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.SkillType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO class for transferring Skill information.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SkillShortDto {

  private long id;
  private String name;
  private SkillType type;
}