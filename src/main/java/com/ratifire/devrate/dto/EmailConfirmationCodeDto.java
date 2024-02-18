package com.ratifire.devrate.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing an email confirmation code.
 */
@Builder
@Getter
public class EmailConfirmationCodeDto {

  private String code;

  private long userId;
}
