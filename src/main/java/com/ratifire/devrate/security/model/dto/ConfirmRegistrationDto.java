package com.ratifire.devrate.security.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for confirming user registration.
 */
@Getter
@Builder
@EqualsAndHashCode
public class ConfirmRegistrationDto {

  @NotBlank(message = "Confirmation code cannot be blank")
  private String confirmationCode;

  @NotBlank(message = "Email cannot be blank")
  private String email;
}