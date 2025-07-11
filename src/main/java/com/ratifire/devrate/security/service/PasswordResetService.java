package com.ratifire.devrate.security.service;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.security.exception.PasswordResetException;
import com.ratifire.devrate.security.model.dto.PasswordResetDto;
import com.ratifire.devrate.service.EmailService;
import com.ratifire.devrate.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing password reset operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile("!local")
public class PasswordResetService {

  private final CognitoApiClientService cognitoApiClientService;
  private final UserService userService;
  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;

  /**
   * Initiates a password reset process for a given user email.
   *
   * @param email the email of the user who is requesting a password reset.
   */
  public void resetPassword(String email) {
    try {
      cognitoApiClientService.resetPassword(email.toLowerCase());
    } catch (Exception e) {
      log.error("Password reset process was failed for email {}: {}", email, e.getMessage());
      throw new PasswordResetException("Password reset process was failed.");
    }
  }

  /**
   * Confirms the password reset process and updates the user's password in the system.
   *
   * @param passwordResetDto the DTO containing the user's email, the confirmation code, and the new
   *                         password.
   */
  @Transactional
  public void confirmResetPassword(PasswordResetDto passwordResetDto) {
    try {

      String code = passwordResetDto.getCode();
      String email = passwordResetDto.getEmail().toLowerCase();
      String newPassword = passwordResetDto.getNewPassword();

      cognitoApiClientService.confirmResetPassword(email, code, newPassword);

      String encodedPassword = passwordEncoder.encode(newPassword);
      User user = userService.findByEmail(email);
      user.setPassword(encodedPassword);

      userService.updateByEntity(user);
      emailService.sendPasswordChangeConfirmationEmail(email);
    } catch (Exception e) {
      log.error("Confirmation password reset process was failed: {}", e.getMessage());
      throw new PasswordResetException("Confirmation password reset process was failed.");
    }
  }
}
