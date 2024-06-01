package com.ratifire.devrate.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the user registration.
 */
@Builder
@Getter
public class UserRegistrationDto {

  @Email
  private String email;

  private String firstName;

  private String lastName;

  private String country;

  private boolean subscribed;

  private String password;

}
