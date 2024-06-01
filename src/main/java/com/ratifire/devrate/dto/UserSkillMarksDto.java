package com.ratifire.devrate.dto;

import com.ratifire.devrate.repository.UserRepository;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the skill marks of a user.
 * This DTO is used to obtain marks from the main mastery using a SQL script {@link UserRepository}.
 */
@Getter
@Setter
@AllArgsConstructor
public class UserSkillMarksDto {
  private BigDecimal hardSkillMark;
  private BigDecimal softSkillMark;
}
