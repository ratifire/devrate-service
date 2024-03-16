package com.ratifire.devrate.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Data transfer object (DTO) representing education.
 */
@Getter
@Builder
public class EducationDto {

  private long id;
  private String type;
  private String name;
  private String description;
  private int startYear;
  private int endYear;
}
