package com.ratifire.devrate.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for skill.
 */
@Getter
@Builder
@EqualsAndHashCode
public class SkillDto {

  private Long id;

  private String name;

}
