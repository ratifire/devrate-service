package com.ratifire.devrate.security.factory;

import com.ratifire.devrate.dto.LoginResponseDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.security.model.enums.LoginStatus;

/**
 * Factory class for creating {@link LoginResponseDto} instances based on login status.
 */
public final class LoginResponseFactory {

  private LoginResponseFactory() {
  }

  public static LoginResponseDto success(UserDto userDto) {
    return create(LoginStatus.AUTHENTICATED, userDto);
  }

  public static LoginResponseDto activationRequired() {
    return create(LoginStatus.ACTIVATION_REQUIRED, null);
  }

  private static LoginResponseDto create(LoginStatus status, UserDto userDto) {
    return LoginResponseDto.builder()
        .status(status)
        .userInfo(userDto)
        .build();
  }

}
