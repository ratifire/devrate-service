package com.ratifire.devrate.security.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for user email change.
 */
@Getter
@EqualsAndHashCode
public class EmailChangeDto {

  @NotBlank(message = "Current email cannot be blank")
  @Size(max = 100)
  @Email
  private String currentEmail;

  @NotBlank(message = "New email cannot be blank")
  @Size(max = 100)
  @Email
  private String newEmail;

}