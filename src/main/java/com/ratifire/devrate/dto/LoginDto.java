package com.ratifire.devrate.dto;

import lombok.Getter;

/**
 * Data transfer object (DTO) representing login credentials.
 */
@Getter
public class LoginDto {

  private String email;
  private String password;
}
