package com.ratifire.devrate.dto;

import com.ratifire.devrate.security.model.enums.LoginStatus;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object representing the login response.
 */
@Builder
@Getter
public class LoginResponseDto {

  private LoginStatus status;
  private UserDto userInfo;
}