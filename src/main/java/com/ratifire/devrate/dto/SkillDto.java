package com.ratifire.devrate.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for Skill.
 */
@Getter
@Builder
@EqualsAndHashCode
public class SkillDto {

  private Long id;

  @NotBlank(message = "Skill name must not be null or empty")
  private String name;

  private BigDecimal averageMark;
}