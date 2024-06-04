package com.ratifire.devrate.service.resetpassword;

import com.ratifire.devrate.dto.PasswordResetDto;
import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.email.EmailService;
import com.ratifire.devrate.service.registration.EmailConfirmationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for password reset logic. This service handles password reset requests
 * and performs necessary operations such as sending reset links and updating user passwords.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetService {

  private final UserSecurityService userSecurityService;
  private final EmailConfirmationCodeService emailConfirmationCodeService;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

  /**
   * Sends a password reset code to the user's email address.
   *
   * @param email The email address associated with the user account.
   */

  @Transactional
  public void requestPasswordReset(String email) {
    UserSecurity userSecurity = userSecurityService.findByEmail(email);
    String code = emailConfirmationCodeService.createConfirmationCode(userSecurity.getId());
    emailService.sendPasswordResetEmail(email, code);
  }

  /**
   * Resets the password of the user associated with the provided confirmation code.
   *
   * @param passwordResetDto The DTO containing the reset password code and the new password.
   */

  @Transactional
  public void resetPassword(PasswordResetDto passwordResetDto) {
    EmailConfirmationCode emailConfirmationCode = emailConfirmationCodeService
        .findEmailConfirmationCode(passwordResetDto.getCode());
    UserSecurity userSecurity = userSecurityService
        .getById(emailConfirmationCode.getUserSecurityId());

    // Check if the confirmation code has expired
    emailConfirmationCodeService.validateAndHandleExpiration(emailConfirmationCode);

    String encodedPassword = passwordEncoder.encode(passwordResetDto.getNewPassword());

    userSecurity.setPassword(encodedPassword);
    userSecurityService.save(userSecurity);

    emailConfirmationCodeService.deleteConfirmedCode(emailConfirmationCode.getId());
    emailService.sendPasswordChangeConfirmation(userSecurity.getEmail());
  }
}