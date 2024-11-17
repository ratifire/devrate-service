package com.ratifire.devrate.security.controller;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.security.model.dto.ConfirmRegistrationDto;
import com.ratifire.devrate.security.model.dto.LoginDto;
import com.ratifire.devrate.security.model.dto.PasswordResetDto;
import com.ratifire.devrate.security.model.dto.UserRegistrationDto;
import com.ratifire.devrate.security.service.AuthenticationService;
import com.ratifire.devrate.security.service.PasswordResetService;
import com.ratifire.devrate.security.service.RefreshTokenService;
import com.ratifire.devrate.security.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  private final AuthenticationService authenticationService;
  private final RegistrationService registrationService;
  private final PasswordResetService passwordResetService;
  private final RefreshTokenService refreshTokenService;

  /**
   * Endpoint for user login.
   *
   * @param loginDto The data transfer object containing the user's login information.
   * @return A UserDto object representing the authenticated user.
   */
  @PostMapping("/signin")
  public UserDto login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
    return authenticationService.login(loginDto, response);
  }

  /**
   * Endpoint for user logout.
   *
   * @param request The HttpServletRequest object representing the HTTP request.
   * @return ResponseEntity representing the result of the logout operation.
   */
  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
    String result = authenticationService.logout(request, response);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  /**
   * Endpoint for user registration.
   *
   * @param userRegistrationDto DTO containing new user's details such as username, password, etc.
   */
  @PostMapping("/signup")
  public void registerUser(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {
    registrationService.registerUser(userRegistrationDto);
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
    long userId = registrationService.confirmRegistration(confirmRegistrationDto);
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
    passwordResetService.resetPassword(email);
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
    passwordResetService.confirmResetPassword(passwordResetDto);
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
    refreshTokenService.refreshAuthTokens(request, response);
    return ResponseEntity.ok().build();
  }
}