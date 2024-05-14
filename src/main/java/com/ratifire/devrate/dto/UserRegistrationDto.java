package com.ratifire.devrate.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the user registration.
 */
@Builder
@Getter
public class UserRegistrationDto {

  private String email;

  private String firstName;

  private String lastName;

  private String country;

  private boolean subscribed;

  private String password;

}
