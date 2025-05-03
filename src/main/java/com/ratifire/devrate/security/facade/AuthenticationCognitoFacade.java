package com.ratifire.devrate.security.facade;

import static com.ratifire.devrate.security.model.enums.OauthIdentityProvider.GOOGLE;
import static com.ratifire.devrate.security.model.enums.OauthIdentityProvider.LINKEDIN;

import com.ratifire.devrate.dto.LoginResponseDto;
import com.ratifire.devrate.security.model.dto.ConfirmActivationAccountDto;
import com.ratifire.devrate.security.model.dto.ConfirmRegistrationDto;
import com.ratifire.devrate.security.model.dto.LoginDto;
import com.ratifire.devrate.security.model.dto.OauthAuthorizationDto;
import com.ratifire.devrate.security.model.dto.PasswordResetDto;
import com.ratifire.devrate.security.model.dto.ResendConfirmCodeDto;
import com.ratifire.devrate.security.model.dto.UserRegistrationDto;
import com.ratifire.devrate.security.service.AuthenticationOauthService;
import com.ratifire.devrate.security.service.AuthenticationService;
import com.ratifire.devrate.security.service.PasswordResetService;
import com.ratifire.devrate.security.service.RefreshTokenService;
import com.ratifire.devrate.security.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
  public LoginResponseDto login(LoginDto loginDto, HttpServletResponse response) {
    return authenticationService.login(loginDto, response);
  }

  @Override
  public LoginResponseDto confirmAccountActivation(ConfirmActivationAccountDto dto,
      HttpServletResponse response, HttpServletRequest request) {
    return authenticationService.confirmAccountActivation(dto, response, request);
  }

  @Override
  public void redirectToLinkedIn(HttpServletResponse response)
      throws IOException {
    String url = authenticationOauthService.generateOauthRedirectUrl(LINKEDIN.getProvider());
    response.sendRedirect(url);
  }

  @Override
  public void redirectToGoogle(HttpServletResponse response)
      throws IOException {
    String url = authenticationOauthService.generateOauthRedirectUrl(GOOGLE.getProvider());
    response.sendRedirect(url);
  }

  @Override
  public LoginResponseDto handleOauthAuthorization(HttpServletResponse response,
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
  public void resendRegistrationConfirmCode(ResendConfirmCodeDto resendConfirmCodeDto) {
    registrationService.resendRegistrationConfirmCode(resendConfirmCodeDto);
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
