package com.ratifire.devrate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the user personal info.
 */
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class UserDto {

  @NotNull
  private long id;

  @Size(max = 100)
  private String firstName;

  @Size(max = 100)
  private String lastName;

  @Size(max = 50)
  private String status;

  @Size(max = 100)
  private String country;

  @Size(max = 100)
  private String city;

  private boolean subscribed;

  private String description;

  private BigDecimal hardSkillMark;

  private BigDecimal softSkillMark;

  private int completedInterviews;

  private int conductedInterviews;
}
