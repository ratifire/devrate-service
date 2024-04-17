package com.ratifire.devrate.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Data transfer object (DTO) representing login credentials.
 */
@Builder
@Getter
public class LoginDto {

  private String email;
  private String password;
}
