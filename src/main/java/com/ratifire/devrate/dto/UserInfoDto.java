package com.ratifire.devrate.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the user personal info.
 */
@Builder
@Getter
public class UserInfoDto {

  private String firstName;

  private String lastName;

  private String position;

  private String country;

  private String state;

  private String city;

  private boolean subscribed;

  private String description;

  private long userId;
}
