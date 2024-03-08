package com.ratifire.devrate.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EducationDto {

  private long id;
  private String university;
  private String courses;
  private String events;
}
