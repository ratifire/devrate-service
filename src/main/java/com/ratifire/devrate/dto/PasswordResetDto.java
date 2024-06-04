package com.ratifire.devrate.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for password reset functionality.
 */
@Builder
@Getter
public class PasswordResetDto {

  private String code;

  private String newPassword;

}
