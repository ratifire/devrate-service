package com.ratifire.devrate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the user details required for sign up or registration.
 * This DTO encapsulates the necessary information needed to create a new user account.
 */
@Builder
@Getter
@Schema(description = "Data transfer object for sign-up necessary information")
public class SignUpDto {

  @Schema(description = "The email chosen by the user for their account")
  private String email;

  @Schema(description = "The first name inputted by user")
  private String firstName;

  @Schema(description = "The last name inputted by user")
  private String lastName;

  @Schema(description = "The country chosen by the user for their account")
  private String country;

  @Schema(description = "Flag indicating whether user is subscribed to newsletter")
  private boolean subscribed;

  @Schema(description = "Flag indicating whether user's email is verified")
  private boolean verified;

  @Schema(description = "The password chosen by the user for their account")
  private String password;

  @Schema(description = "Timestamp when sign-up information was created")
  private LocalDateTime createdAt;

}
