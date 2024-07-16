package com.ratifire.devrate.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for Mastery.
 */
@Getter
@Builder
@EqualsAndHashCode
public class MasteryDto {

  private Long id;

  @NotBlank(message = "Mastery name must not be null or empty")
  private String level;

  private BigDecimal softSkillMark;

  private BigDecimal hardSkillMark;
}
