package com.ratifire.devrate.dto;

import com.ratifire.devrate.entity.User;
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

  private boolean isSubscribed;

  private boolean isVerified;

  private String password;

  /**
   * Converts this SignUpDto object into a User object.
   *
   * @return A User object with the same details as this SignUpDto.
   */
  public User toUser() {
    return User.builder()
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .country(country)
        .isSubscribed(isSubscribed)
        .isVerified(isVerified)
        .createdAt(LocalDateTime.now())
        .build();
  }
}
