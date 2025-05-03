package com.ratifire.devrate.security.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for confirming user activation account data.
 */
@Getter
@EqualsAndHashCode
public class ConfirmActivationAccountDto {

  @NotBlank(message = "Activation code cannot be blank")
  private String activationCode;

  private String password;
}