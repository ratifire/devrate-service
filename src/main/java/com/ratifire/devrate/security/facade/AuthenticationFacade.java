package com.ratifire.devrate.security.facade;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.security.model.dto.ConfirmRegistrationDto;
import com.ratifire.devrate.security.model.dto.LoginDto;
import com.ratifire.devrate.security.model.dto.OauthExchangeCodeRequest;
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

  void loginLinkedIn(HttpServletResponse response, HttpSession session) throws IOException;

  void loginGoogle(HttpServletResponse response, HttpSession session) throws IOException;

  UserDto exchangeAuthCode(HttpServletResponse response, OauthExchangeCodeRequest request);

  String logout(HttpServletRequest request, HttpServletResponse response);

  void registerUser(UserRegistrationDto userRegistrationDto);

  long confirmRegistration(ConfirmRegistrationDto confirmationCodeDto);

  void confirmResetPassword(PasswordResetDto passwordResetDto);

  void resetPassword(String email);

  void refreshAuthTokens(HttpServletRequest request, HttpServletResponse response);
}
