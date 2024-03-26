package com.ratifire.devrate.dto;

import com.ratifire.devrate.entity.Education;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the user personal info.
 */
@Builder
@Getter
public class UserInfoDto {

  @Size(max = 100)
  private String firstName;

  @Size(max = 100)
  private String lastName;

  @Size(max = 50)
  private String position;

  @Size(max = 100)
  private String country;

  @Size(max = 100)
  private String region;

  @Size(max = 100)
  private String city;

  private boolean subscribed;

  private String description;

  private List<EducationDto> educations;

  @NotNull
  private long userId;
}
