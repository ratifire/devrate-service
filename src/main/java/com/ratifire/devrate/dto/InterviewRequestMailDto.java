package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.InterviewRequestRole;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

/**
 * DTO for passing only the required interview request data to EmailService.
 */
@Builder
public record InterviewRequestMailDto(
    long userId,
    String firstName,
    String lastName,
    InterviewRequestRole role,
    String specializationName,
    List<String> skillNames,
    BigDecimal softSkillMark,
    BigDecimal hardSkillMark
) {}
