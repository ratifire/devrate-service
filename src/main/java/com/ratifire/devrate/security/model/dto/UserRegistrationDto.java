package com.ratifire.devrate.security.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the user registration.
 */
@Builder
@Getter
public class UserRegistrationDto {

  private static final String NAME_PATTERN = "^[\\p{L}\\s\\-']+$";
  private static final String PASSWORD_PATTERN =
      "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s])(?!.*\\s).{6,}$";

  @NotBlank(message = "Email cannot be blank")
  @Size(max = 100)
  @Email
  private String email;

  @NotBlank(message = "First name cannot be blank")
  @Size(max = 50)
  @Pattern(regexp = NAME_PATTERN, message = "First name can only contain letters, spaces, hyphens,"
      + " and apostrophes")
  private String firstName;

  @NotBlank(message = "Last name cannot be blank")
  @Size(max = 50)
  @Pattern(regexp = NAME_PATTERN, message = "Last name can only contain letters, spaces, hyphens, "
      + "and apostrophes")
  private String lastName;

  @NotBlank(message = "Country name cannot be blank")
  @Size(max = 100)
  private String country;

  private boolean subscribed;

  @NotBlank(message = "Password cannot be blank")
  @Pattern(regexp = PASSWORD_PATTERN,
      message = "Password must be at least 6 characters long, "
          + "contain at least one uppercase letter, "
          + "one number, and one special character.")
  private String password;
}
