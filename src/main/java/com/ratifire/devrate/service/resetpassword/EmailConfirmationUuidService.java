package com.ratifire.devrate.service.resetpassword;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.exception.InvalidCodeException;
import com.ratifire.devrate.repository.EmailConfirmationCodeRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
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

  /**
   * Generates a unique UUID.
   *
   * @return A unique UUID string.
   */
  public String createUniqueUuid() {
    return UUID.randomUUID().toString();
  }

  /**
   * Saves the UUID confirmation code for a specific user security in the database.
   *
   * @param userSecurityId The ID of the user security associated with the confirmation code.
   * @param code   The confirmation code to be saved.
   */
  public void saveUuidConfirmationCode(Long userSecurityId, String code) {
    LocalDateTime createdAt = LocalDateTime.now().plusHours(12);
    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode.builder()
            .code(code)
            .createdAt(createdAt)
            .userSecurityId(userSecurityId)
            .build();
    emailConfirmationCodeRepository.save(emailConfirmationCode);
  }

  /**
   * Generates and persists a unique UUID confirmation code for email confirmation.
   *
   * @param userSecurityId ID of the user security to generate the code for.
   * @return Generated UUID confirmation code.
   */
  public String generateAndPersistUuidCode(Long userSecurityId) {
    deleteConfirmedCodesByUserSecurityId(userSecurityId);
    String code = createUniqueUuid();
    saveUuidConfirmationCode(userSecurityId, code);
    return code;
  }

  /**
   * Removes a confirmed email code for the specified user security ID.
   *
   * <p>This method deletes a previously confirmed email code from the database.
   *
   * @param userSecurityId The ID of the user security whose email code is to be deleted.
   */
  public void deleteConfirmedCodesByUserSecurityId(Long userSecurityId) {
    emailConfirmationCodeRepository.deleteByUserSecurityId(userSecurityId);
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