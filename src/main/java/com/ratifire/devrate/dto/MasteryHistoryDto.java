package com.ratifire.devrate.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for Mastery History.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MasteryHistoryDto {
  private Long masteryId;
  private LocalDate date;
  private BigDecimal hardSkillMark;
  private BigDecimal softSkillMark;
}