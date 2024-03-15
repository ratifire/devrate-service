package com.ratifire.devrate.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EducationDto {

  private long id;
  private String educationType;
  private String educationName;
  private String description;
  private int startYear;
  private int endYear;
}
