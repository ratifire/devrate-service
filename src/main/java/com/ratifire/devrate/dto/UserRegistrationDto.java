package com.ratifire.devrate.dto;

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

  @NotBlank(message = "Email cannot be blank")
  @Size(max = 100)
  @Email
  private String email;

  @NotBlank(message = "First name cannot be blank")
  @Size(max = 100)
  @Pattern(regexp = "^[a-zA-Zа-щА-ЩґҐєЄіІїЇьЬ\\s\\-']+$", message = "First name can only contain "
      + "letters, spaces, hyphens, and apostrophes")
  private String firstName;

  @NotBlank(message = "Last name cannot be blank")
  @Size(max = 100)
  @Pattern(regexp = "^[a-zA-Zа-щА-ЩґҐєЄіІїЇьЬ\\s\\-']+$", message = "Last name can only contain "
      + "letters, spaces, hyphens, and apostrophes")
  private String lastName;

  @NotBlank(message = "Country name cannot be blank")
  @Size(max = 100)
  private String country;

  private boolean subscribed;

  @NotBlank(message = "Password cannot be blank")
  private String password;

}
