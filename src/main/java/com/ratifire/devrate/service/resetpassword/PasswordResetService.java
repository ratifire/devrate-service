package com.ratifire.devrate.service.resetpassword;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.email.EmailService;
import com.ratifire.devrate.service.registration.EmailConfirmationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
  public boolean requestPasswordReset(String email) {
    UserSecurity userSecurity = userSecurityService.findByEmail(email);
    String code = emailConfirmationCodeService.createConfirmationCode(userSecurity.getId());
    emailService.sendPasswordResetEmail(email, code);
    return true;
  }

  /**
   * Resets the password of the user associated with the provided confirmation code.
   *
   * @param code        The reset password code.
   * @param newPassword The new password to be set for the user account.
   */
  public boolean resetPassword(String code, String newPassword) {
    EmailConfirmationCode emailConfirmationCode = emailConfirmationCodeService
        .findEmailConfirmationCode(code);
    UserSecurity userSecurity = userSecurityService
        .getById(emailConfirmationCode.getUserSecurityId());

    // Check if the confirmation code has expired
    emailConfirmationCodeService.validateAndHandleExpiration(emailConfirmationCode);

    String encodedPassword = passwordEncoder.encode(newPassword);

    userSecurity.setPassword(encodedPassword);
    userSecurityService.save(userSecurity);

    emailConfirmationCodeService.deleteConfirmedCode(emailConfirmationCode.getId());
    emailService.sendPasswordChangeConfirmation(userSecurity.getEmail());
    return true;
  }
}