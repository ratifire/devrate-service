package com.ratifire.devrate.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the user registration.
 */
@Builder
@Getter
public class UserDto {

  private String email;

  private String firstName;

  private String lastName;

  private String country;

  private boolean subscribed;

  private boolean verified;

  private String password;

  private LocalDateTime createdAt;

}
