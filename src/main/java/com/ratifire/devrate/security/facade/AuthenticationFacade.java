package com.ratifire.devrate.security.facade;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.security.model.dto.ConfirmRegistrationDto;
import com.ratifire.devrate.security.model.dto.LoginDto;
import com.ratifire.devrate.security.model.dto.PasswordResetDto;
import com.ratifire.devrate.security.model.dto.ResendConfirmCodeDto;
import com.ratifire.devrate.security.model.dto.UserRegistrationDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Authentication facade interface.
 */
public interface AuthenticationFacade {

  UserDto login(LoginDto loginDto, HttpServletResponse response);

  String logout(HttpServletRequest request, HttpServletResponse response);

  void registerUser(UserRegistrationDto userRegistrationDto);

  long confirmRegistration(ConfirmRegistrationDto confirmationCodeDto);

  void confirmResetPassword(PasswordResetDto passwordResetDto);

  void resetPassword(String email);

  void refreshAuthTokens(HttpServletRequest request, HttpServletResponse response);

  void resendRegistrationConfirmCode(ResendConfirmCodeDto resendConfirmCodeDto);
}
