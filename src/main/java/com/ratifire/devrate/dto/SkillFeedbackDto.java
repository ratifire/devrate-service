package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.SkillType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO class for transferring feedback skills data.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SkillFeedbackDto {

  @NotNull
  private long id;

  @NotNull
  @DecimalMin(value = "1")
  @DecimalMax(value = "10")
  private BigDecimal mark;

  @NotNull
  private String name;

  @NotNull
  private SkillType type;

}