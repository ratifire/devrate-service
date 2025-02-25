package com.ratifire.devrate.security.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for resent confirming user registration.
 */
@Getter
public class ResendConfirmCodeDto {

  @NotBlank(message = "Email cannot be blank")
  private String email;
}