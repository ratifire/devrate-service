package com.ratifire.devrate.security.service;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.security.model.dto.PasswordResetDto;
import com.ratifire.devrate.service.email.EmailService;
import com.ratifire.devrate.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing password reset operations.
 */
@Service
@RequiredArgsConstructor
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
    cognitoApiClientService.resetPassword(email);
  }

  /**
   * Confirms the password reset process and updates the user's password in the system.
   *
   * @param passwordResetDto the DTO containing the user's email, the confirmation code, and the new
   *                         password.
   */
  @Transactional
  public void confirmResetPassword(PasswordResetDto passwordResetDto) {
    String code = passwordResetDto.getCode();
    String email = passwordResetDto.getEmail();
    String newPassword = passwordResetDto.getNewPassword();

    cognitoApiClientService.confirmResetPassword(email, code, newPassword);

    String encodedPassword = passwordEncoder.encode(newPassword);
    User user = userService.findByEmail(email);
    user.setPassword(encodedPassword);

    userService.updateUser(user);
    emailService.sendPasswordChangeConfirmation(email);
  }
}