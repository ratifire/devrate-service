package com.ratifire.devrate.security.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for password reset functionality.
 */
@Builder
@Getter
public class PasswordResetDto {

  @NotBlank(message = "Email cannot be blank")
  private String email;

  @NotBlank(message = "Confirmation code cannot be blank")
  private String code;

  @NotBlank(message = "New password cannot be blank")
  private String newPassword;

}