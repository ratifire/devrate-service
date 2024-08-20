package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.LoginDto;
import com.ratifire.devrate.dto.PasswordResetDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.dto.UserRegistrationDto;
import com.ratifire.devrate.service.AuthenticationService;
import com.ratifire.devrate.service.registration.RegistrationService;
import com.ratifire.devrate.service.resetpassword.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
public class AuthController {

  private final AuthenticationService authenticationService;
  private final RegistrationService registrationService;
  private final PasswordResetService passwordResetService;

  /**
   * Endpoint for user login.
   *
   * @param loginDto The data transfer object containing the user's login information.
   * @return A UserDto object representing the authenticated user.
   */
  @PostMapping("/signin")
  public UserDto login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
    return authenticationService.login(loginDto, request);
  }

  /**
   * Endpoint for user logout.
   *
   * @param request The HttpServletRequest object representing the HTTP request.
   * @return ResponseEntity representing the result of the logout operation.
   */
  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request) {
    return authenticationService.logout(request);
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
   * @param code Unique code used for confirming user registration.
   * @return ResponseEntity with the user ID.
   */
  @PutMapping("/signup/{code}")
  public ResponseEntity<Long> confirm(@PathVariable String code) {
    long userId = registrationService.confirmRegistration(code);
    return ResponseEntity.status(HttpStatus.CREATED).body(userId);
  }

  /**
   * Endpoint for requesting a password reset code.
   *
   * @param email The email for the user account needing a password reset.
   * @return ResponseEntity with status OK if successful.
   */
  @PostMapping ("/request-password-reset")
  public ResponseEntity<Void> requestPasswordReset(@RequestParam String email) {
    passwordResetService.requestPasswordReset(email);
    return ResponseEntity.ok().build();
  }

  /**
   * Endpoint for resetting the password using a unique code.
   *
   * @param passwordResetDto The DTO containing the reset password code and the new password.
   * @return ResponseEntity with status OK if successful.
   */
  @PostMapping("/password-reset")
  public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
    passwordResetService.resetPassword(passwordResetDto);
    return ResponseEntity.ok().build();
  }
}
