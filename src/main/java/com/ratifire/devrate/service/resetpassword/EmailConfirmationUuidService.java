package com.ratifire.devrate.service.resetpassword;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.exception.InvalidCodeException;
import com.ratifire.devrate.repository.EmailConfirmationCodeRepository;
import com.ratifire.devrate.service.email.EmailService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;



/**
 * Service for generating unique UUID codes for email verification.
 */

@Service
@AllArgsConstructor
public class EmailConfirmationUuidService {

  /**
   * Repository for accessing email confirmation code data in the database.
   */
  private final EmailConfirmationCodeRepository emailConfirmationCodeRepository;
  private final EmailService emailService;

  /**
   * Generates a unique UUID.
   *
   * @return A unique UUID string.
   */
  public String createUniqueUuid() {
    return UUID.randomUUID().toString();
  }

  /**
   * Saves the UUID confirmation code for a specific user in the database.
   *
   * @param userId The ID of the user associated with the confirmation code.
   * @param code   The confirmation code to be saved.
   */
  public void saveUuidConfirmationCode(Long userId, String code) {
    LocalDateTime createdAt = LocalDateTime.now().plusHours(12);
    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode.builder()
            .code(code)
            .createdAt(createdAt)
            .userSecurityId(userId)
            .build();
    emailConfirmationCodeRepository.save(emailConfirmationCode);
  }

  /**
   * Generates and persists a unique UUID confirmation code for email confirmation.
   *
   * @param userId ID of the user to generate the code for.
   * @return Generated UUID confirmation code.
   */

  public String generateAndPersistUuidCode(Long userId) {
    deleteConfirmedCodesByUserId(userId);
    String code = createUniqueUuid();
    saveUuidConfirmationCode(userId, code);
    return code;
  }

  /**
   * Removes a confirmed email code for the specified user ID.
   *
   * <p>This method deletes a previously confirmed email code from the database.
   *
   * @param userId The ID of the user whose email code is to be deleted.
   */

  public void deleteConfirmedCodesByUserId(Long userId) {
    emailConfirmationCodeRepository.deleteByUserId(userId);
  }

  /**
   * Sends an email with a password reset link to the user.
   *
   * @param email The user's email address.
   * @param code  The unique password reset code.
   */
  public void sendPasswordResetEmail(String email, String code) {
    // Temporarily removed password reset URL
    String resetLink = "#" + code;
    SimpleMailMessage resetEmail = new SimpleMailMessage();
    resetEmail.setTo(email);
    resetEmail.setSubject("Password Reset");
    resetEmail.setText("To reset your password, please click the link below:\n" + resetLink);
    emailService.sendEmail(resetEmail, false);
  }

  /**
   * Sends an email confirmation about the password change.
   *
   * @param email The user's email address.
   */
  public void sendPasswordChangeConfirmation(String email) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("Password Successfully Reset");
    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String text = "Your password has been successfully changed on " + formattedDateTime + ".";
    message.setText(text);
    emailService.sendEmail(message, false);
  }

  /**
   * Finds and retrieves the UUID confirmation code from the database based on the provided code.
   *
   * @param code The confirmation code to search for.
   * @return The EmailConfirmationCode entity associated with the provided code.
   * @throws InvalidCodeException If the code is invalid or expired.
   */
  public EmailConfirmationCode findUuidCode(String code) {
    return emailConfirmationCodeRepository.findByCode(code)
            .orElseThrow(() -> new InvalidCodeException("Invalid or expired password reset code."));
  }
}