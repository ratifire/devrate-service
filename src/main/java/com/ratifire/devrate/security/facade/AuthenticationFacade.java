package com.ratifire.devrate.security.facade;

import com.ratifire.devrate.dto.LoginResponseDto;
import com.ratifire.devrate.security.model.dto.ConfirmActivationAccountDto;
import com.ratifire.devrate.security.model.dto.ConfirmRegistrationDto;
import com.ratifire.devrate.security.model.dto.LoginDto;
import com.ratifire.devrate.security.model.dto.OauthAuthorizationDto;
import com.ratifire.devrate.security.model.dto.PasswordResetDto;
import com.ratifire.devrate.security.model.dto.ResendConfirmCodeDto;
import com.ratifire.devrate.security.model.dto.UserRegistrationDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Authentication facade interface.
 */
public interface AuthenticationFacade {

  LoginResponseDto login(LoginDto loginDto, HttpServletResponse response);

  LoginResponseDto confirmAccountActivation(ConfirmActivationAccountDto confirmActivationAccountDto,
      HttpServletResponse response, HttpServletRequest request);

  void resendActivationAccountConfirmCode(ResendConfirmCodeDto resendConfirmCodeDto);

  void redirectToLinkedIn(HttpServletResponse response) throws IOException;

  void redirectToGoogle(HttpServletResponse response) throws IOException;

  LoginResponseDto handleOauthAuthorization(HttpServletResponse response,
      OauthAuthorizationDto request);

  String logout(HttpServletRequest request, HttpServletResponse response);

  void registerUser(UserRegistrationDto userRegistrationDto);

  long confirmRegistration(ConfirmRegistrationDto confirmationCodeDto);

  void confirmResetPassword(PasswordResetDto passwordResetDto);

  void resetPassword(String email);

  void refreshAuthTokens(HttpServletRequest request, HttpServletResponse response);

  void resendRegistrationConfirmCode(ResendConfirmCodeDto resendConfirmCodeDto);
}
