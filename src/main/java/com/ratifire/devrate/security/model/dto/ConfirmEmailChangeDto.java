package com.ratifire.devrate.security.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for confirming user email change.
 */
@Getter
@EqualsAndHashCode
public class ConfirmEmailChangeDto {

  @NotBlank(message = "Confirmation code cannot be blank")
  private String confirmationCode;

}