package com.ratifire.devrate.security.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for password reset functionality.
 */
@Builder
@Getter
public class PasswordResetDto {

  private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)"
      + "(?=.*[!@#$&*])[A-Za-z\\d!@#$&*]{8,}$";

  @NotBlank(message = "Email cannot be blank")
  private String email;

  @NotBlank(message = "Confirmation code cannot be blank")
  private String code;

  @NotBlank(message = "New password cannot be blank")
  @Pattern(regexp = PASSWORD_PATTERN,
      message = "Password must be at least 8 characters long, "
          + "contain at least one lowercase letter, "
          + "one uppercase letter, one number, and one special "
          + "character (!@#$&*), and only Latin letters are allowed.")
  private String newPassword;

}