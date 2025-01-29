package com.ratifire.devrate.security.facade;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.security.model.dto.ConfirmRegistrationDto;
import com.ratifire.devrate.security.model.dto.LoginDto;
import com.ratifire.devrate.security.model.dto.OauthAuthorizationDto;
import com.ratifire.devrate.security.model.dto.PasswordResetDto;
import com.ratifire.devrate.security.model.dto.UserRegistrationDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Authentication facade interface.
 */
public interface AuthenticationFacade {

  UserDto login(LoginDto loginDto, HttpServletResponse response);

  void redirectToLinkedIn(HttpServletResponse response, HttpSession session) throws IOException;

  void redirectToGoogle(HttpServletResponse response, HttpSession session) throws IOException;

  UserDto handleOauthAuthorization(HttpServletResponse response,
      OauthAuthorizationDto request);

  String logout(HttpServletRequest request, HttpServletResponse response);

  void registerUser(UserRegistrationDto userRegistrationDto);

  long confirmRegistration(ConfirmRegistrationDto confirmationCodeDto);

  void confirmResetPassword(PasswordResetDto passwordResetDto);

  void resetPassword(String email);

  void refreshAuthTokens(HttpServletRequest request, HttpServletResponse response);
}
