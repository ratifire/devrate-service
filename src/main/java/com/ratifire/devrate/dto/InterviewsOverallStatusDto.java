package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.InterviewStatusIndicator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for representing interviews overall status on the UI.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewsOverallStatusDto {

  private InterviewStatusIndicator status;
}
