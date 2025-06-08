package com.ratifire.devrate.security.controller;

import com.ratifire.devrate.dto.LoginResponseDto;
import com.ratifire.devrate.security.facade.AuthenticationFacade;
import com.ratifire.devrate.security.model.dto.ConfirmActivationAccountDto;
import com.ratifire.devrate.security.model.dto.ConfirmRegistrationDto;
import com.ratifire.devrate.security.model.dto.LoginDto;
import com.ratifire.devrate.security.model.dto.OauthAuthorizationDto;
import com.ratifire.devrate.security.model.dto.PasswordResetDto;
import com.ratifire.devrate.security.model.dto.ResendConfirmCodeDto;
import com.ratifire.devrate.security.model.dto.UserRegistrationDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling authentication-related endpoints.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationFacade authenticationFacade;

  /**
   * Endpoint for user login.
   *
   * @param loginDto The data transfer object containing the user's login information.
   * @return A UserDto object representing the authenticated user.
   */
  @PostMapping("/signin")
  public LoginResponseDto login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
    return authenticationFacade.login(loginDto, response);
  }

  @PostMapping("/confirm-activation-account")
  public LoginResponseDto confirmActivationAccount(@RequestBody ConfirmActivationAccountDto dto,
      HttpServletResponse response, HttpServletRequest request) {
    return authenticationFacade.confirmAccountActivation(dto, response, request);
  }

  /**
   * Resends the confirmation code for user activation account.
   *
   * @param resendConfirmCodeDto The DTO containing the user's email.
   */
  @PostMapping("/activation-account/resend-code")
  public void resendActivationAccountConfirmCode(
      @RequestBody @Valid ResendConfirmCodeDto resendConfirmCodeDto) {
    authenticationFacade.resendActivationAccountConfirmCode(resendConfirmCodeDto);
  }

  /**
   * Redirects the user to LinkedIn for OAuth authentication.
   *
   * @param response the HttpServletResponse object required to send the redirect
   */
  @GetMapping("/oauth/redirect/linkedIn")
  public void redirectToLinkedIn(HttpServletResponse response) throws IOException {
    authenticationFacade.redirectToLinkedIn(response);
  }

  /**
   * Redirects the user to Google's OAuth authorization page.
   *
   * @param response The HttpServletResponse used to redirect the user.
   */
  @GetMapping("/oauth/redirect/google")
  public void redirectToGoogle(HttpServletResponse response) throws IOException {
    authenticationFacade.redirectToGoogle(response);
  }

  /**
   * Handles the OAuth authorization request, processing the provided authorization details and
   * returning the resulting user details and tokens.
   *
   * @param response the HttpServletResponse object to send HTTP responses
   * @param request  the OAuth authorization request
   * @return the UserDto object containing details of the authenticated user
   */
  @PostMapping("/oauth/authorize")
  public LoginResponseDto handleOauthAuthorization(HttpServletResponse response,
      @Valid @RequestBody OauthAuthorizationDto request) {
    return authenticationFacade.handleOauthAuthorization(response, request);
  }

  /**
   * Endpoint for user logout.
   *
   * @param request The HttpServletRequest object representing the HTTP request.
   * @return ResponseEntity representing the result of the logout operation.
   */
  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
    String result = authenticationFacade.logout(request, response);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  /**
   * Endpoint for user registration.
   *
   * @param userRegistrationDto DTO containing new user's details such as username, password, etc.
   */
  @PostMapping("/signup")
  public void registerUser(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {
    authenticationFacade.registerUser(userRegistrationDto);
  }

  /**
   * Resends the confirmation code for user registration.
   *
   * @param resendConfirmCodeDto The DTO containing the user's email.
   */
  @PostMapping("/signup/resend-code")
  public void resendConfirmCode(
      @RequestBody @Valid ResendConfirmCodeDto resendConfirmCodeDto) {
    authenticationFacade.resendRegistrationConfirmCode(resendConfirmCodeDto);
  }

  /**
   * Endpoint to confirm the email using the provided confirmation code.
   *
   * @param confirmRegistrationDto DTO containing the confirmation code and email of the user.
   * @return ResponseEntity with the user ID.
   */
  @PutMapping("/signup/confirm")
  public ResponseEntity<Long> confirm(@RequestBody @Valid ConfirmRegistrationDto
      confirmRegistrationDto) {
    long userId = authenticationFacade.confirmRegistration(confirmRegistrationDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(userId);
  }

  /**
   * Endpoint for requesting a password reset code.
   *
   * @param email The email for the user account needing a password reset.
   * @return ResponseEntity with status OK if successful.
   */
  @PostMapping("/request-password-reset")
  public ResponseEntity<Void> resetPassword(@RequestParam String email) {
    authenticationFacade.resetPassword(email);
    return ResponseEntity.ok().build();
  }

  /**
   * Endpoint for resetting the password using a unique code.
   *
   * @param passwordResetDto The DTO containing the reset password code and the new password.
   * @return ResponseEntity with status OK if successful.
   */
  @PostMapping("/password-reset")
  public ResponseEntity<Void> confirmResetPassword(@RequestBody PasswordResetDto passwordResetDto) {
    authenticationFacade.confirmResetPassword(passwordResetDto);
    return ResponseEntity.ok().build();
  }

  /**
   * Handles the refresh token operation.
   *
   * @return a ResponseEntity with an HTTP 200 status indicating that the token was successfully
   *     refreshed.
   */
  @PostMapping("/refresh-token")
  public ResponseEntity<Void> refreshToken(HttpServletRequest request,
      HttpServletResponse response) {
    authenticationFacade.refreshAuthTokens(request, response);
    return ResponseEntity.ok().build();
  }
}
