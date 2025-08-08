package com.ratifire.devrate.security.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for user password change.
 */
@Getter
@EqualsAndHashCode
public class PasswordChangeDto {

  private static final String PASSWORD_PATTERN =
      "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s])(?!.*\\s).{6,}$";

  @NotBlank(message = "Current password cannot be blank")
  private String currentPassword;

  @NotBlank(message = "Password cannot be blank")
  @Pattern(regexp = PASSWORD_PATTERN,
      message = "Password must be at least 6 characters long, "
          + "contain at least one uppercase letter, "
          + "one number, and one special character.")
  private String newPassword;

}