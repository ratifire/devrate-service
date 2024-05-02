package com.ratifire.devrate.dto;

import com.ratifire.devrate.entity.Specialisation;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for skill.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SkillDto {

  private Long id;

  @NotNull
  private String specialisation;

  private List<String> skills;
}
