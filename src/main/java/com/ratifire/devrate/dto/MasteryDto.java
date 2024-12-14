package com.ratifire.devrate.dto;

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

  private int level;

  private BigDecimal softSkillMark;

  private BigDecimal hardSkillMark;
}
