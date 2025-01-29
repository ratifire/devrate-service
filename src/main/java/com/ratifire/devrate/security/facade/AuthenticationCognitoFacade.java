package com.ratifire.devrate.security.facade;

import static com.ratifire.devrate.security.model.enums.OauthProvider.GOOGLE;
import static com.ratifire.devrate.security.model.enums.OauthProvider.LINKEDIN;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.security.model.dto.ConfirmRegistrationDto;
import com.ratifire.devrate.security.model.dto.LoginDto;
import com.ratifire.devrate.security.model.dto.OauthAuthorizationDto;
import com.ratifire.devrate.security.model.dto.PasswordResetDto;
import com.ratifire.devrate.security.model.dto.UserRegistrationDto;
import com.ratifire.devrate.security.service.AuthenticationOauthService;
import com.ratifire.devrate.security.service.AuthenticationService;
import com.ratifire.devrate.security.service.PasswordResetService;
import com.ratifire.devrate.security.service.RefreshTokenService;
import com.ratifire.devrate.security.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * AWS Cognito facade class.
 */
@RequiredArgsConstructor
@Component
@Profile("!local")
public class AuthenticationCognitoFacade implements AuthenticationFacade {

  private final AuthenticationService authenticationService;
  private final AuthenticationOauthService authenticationOauthService;
  private final RegistrationService registrationService;
  private final PasswordResetService passwordResetService;
  private final RefreshTokenService refreshTokenService;

  @Override
  public UserDto login(LoginDto loginDto, HttpServletResponse response) {
    return authenticationService.login(loginDto, response);
  }

  @Override
  public void redirectToLinkedIn(HttpServletResponse response, HttpSession session)
      throws IOException {
    String url = authenticationOauthService.generateOauthRedirectUrl(session,
        LINKEDIN.getProvider());
    response.sendRedirect(url);
  }

  @Override
  public void redirectToGoogle(HttpServletResponse response, HttpSession session)
      throws IOException {
    String url = authenticationOauthService.generateOauthRedirectUrl(session,
        GOOGLE.getProvider());
    response.sendRedirect(url);
  }

  @Override
  public UserDto handleOauthAuthorization(HttpServletResponse response,
      OauthAuthorizationDto request) {
    return authenticationOauthService.handleOauthAuthorization(response, request);
  }

  @Override
  public String logout(HttpServletRequest request, HttpServletResponse response) {
    return authenticationService.logout(request, response);
  }

  @Override
  public void registerUser(UserRegistrationDto userRegistrationDto) {
    registrationService.registerUser(userRegistrationDto);
  }

  @Override
  public long confirmRegistration(ConfirmRegistrationDto confirmationCodeDto) {
    return registrationService.confirmRegistration(confirmationCodeDto);
  }

  @Override
  public void confirmResetPassword(PasswordResetDto passwordResetDto) {
    passwordResetService.confirmResetPassword(passwordResetDto);
  }

  @Override
  public void resetPassword(String email) {
    passwordResetService.resetPassword(email);
  }

  @Override
  public void refreshAuthTokens(HttpServletRequest request, HttpServletResponse response) {
    refreshTokenService.refreshAuthTokens(request, response);
  }
}
