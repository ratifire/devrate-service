package com.ratifire.devrate.controller;

import com.ratifire.devrate.service.resetpassword.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling password reset requests.
 * Offers endpoints for:
 * - Requesting password reset code.
 * - Resetting passwords with a unique code (UUID).
 */
@RestController
@RequestMapping("/auth/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {

  private final PasswordResetService passwordResetService;

  /**
   * Endpoint for requesting a password reset code.
   *
   * @param email The email for the user account needing a password reset.
   * @return true if successful, false otherwise.
   */
  @PostMapping()
  public boolean requestPasswordReset(@RequestParam String email) {
    return passwordResetService.requestPasswordReset(email);
  }

  /**
   * Endpoint for resetting the password using a unique code (UUID).
   *
   * @param code The unique code received by the email.
   * @param newPassword The new password to be set for the user account.
   * @return true if the password was successfully reset, false otherwise.
   */
  @PostMapping("/{code}")
  public boolean resetPassword(@PathVariable String code, @RequestBody String newPassword) {
    return passwordResetService.resetPassword(code, newPassword);
  }
}
