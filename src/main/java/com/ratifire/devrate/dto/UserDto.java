package com.ratifire.devrate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the user personal info.
 */
@Builder
@Getter
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
  private String region;

  @Size(max = 100)
  private String city;

  private boolean subscribed;

  private String description;
}
