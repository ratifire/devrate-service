package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.SkillType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

  @Size(max = 100, message = "Skill name must not be longer than 100 characters")
  @NotBlank(message = "Skill name must not be null or empty")
  private String name;

  private SkillType type;

  private BigDecimal averageMark;

  private boolean hidden;

  private boolean grows;
}