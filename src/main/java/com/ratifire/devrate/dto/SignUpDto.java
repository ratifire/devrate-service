package com.ratifire.devrate.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the user details required for sign up or registration.
 * This DTO encapsulates the necessary information needed to create a new user account.
 */
@Builder
@Getter
public class SignUpDto {

  private String email;

  private String firstName;

  private String lastName;

  private String country;

  private boolean subscribed;

  private boolean verified;

  private String password;

  private LocalDateTime createdAt;

}
