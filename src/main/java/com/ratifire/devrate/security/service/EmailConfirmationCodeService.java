package com.ratifire.devrate.security.service;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.exception.MailConfirmationCodeException;
import com.ratifire.devrate.exception.MailConfirmationCodeExpiredException;
import com.ratifire.devrate.repository.EmailConfirmationCodeRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for email confirmation logic.
 */
@RequiredArgsConstructor
@Service
public class EmailConfirmationCodeService {

  private static final int BOUND = 999999;
  private static final long CONFIRM_CODE_EXPIRATION_HOURS = 24;
  private final EmailConfirmationCodeRepository emailConfirmationCodeRepository;

  /**
   * Generates a confirmation code, creates an {@code EmailConfirmationCode} object, and saves it
   * for the specified user.
   *
   * @param userId The unique identifier of the user.
   * @return The {@link EmailConfirmationCode} entity saved by the specified user ID.
   */
  public String createConfirmationCode(long userId) {
    String code = generateConfirmationCode();
    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode.builder()
        .code(code)
        .createdAt(LocalDateTime.now())
        .userId(userId)
        .build();
    emailConfirmationCodeRepository.save(emailConfirmationCode);
    return code;
  }

  /**
   * Generates a random 6-digit confirmation code.
   *
   * @return A randomly generated 6-digit confirmation code as a string.
   */
  protected String generateConfirmationCode() {
    return String.format("%06d", new SecureRandom().nextInt(BOUND));
  }

  /**
   * Retrieves the {@link EmailConfirmationCode} entity for a requested code.
   *
   * @return The {@link EmailConfirmationCode} entity associated with the specified user ID.
   * @throws MailConfirmationCodeException If the confirmation code for the user ID is not found.
   */
  public EmailConfirmationCode findByCode(String code) {
    return emailConfirmationCodeRepository.findByCode(code)
        .orElseThrow(() -> new MailConfirmationCodeException("The confirmation code "
            + "\"" + code + "\" could not be found."));
  }

  /**
   * Retrieves the {@link EmailConfirmationCode} entity for a requested user ID.
   *
   * @return The {@link EmailConfirmationCode} entity associated with the specified user ID.
   */
  public EmailConfirmationCode findByUserId(long userId) {
    return emailConfirmationCodeRepository.findByUserId(userId)
        .orElseThrow(() ->
            new MailConfirmationCodeException("The confirmation code for user not found"));
  }

  /**
   * Validates whether the given email confirmation code has expired and handles the expiration if
   * it has.
   *
   * @param emailConfirmationCode the email confirmation code to be validated
   * @throws MailConfirmationCodeExpiredException if the confirmation code has expired
   */
  public void validateExpiration(EmailConfirmationCode emailConfirmationCode) {
    LocalDateTime createdAt = emailConfirmationCode.getCreatedAt();
    LocalDateTime currentDateTime = LocalDateTime.now();

    long hoursSinceCreation = ChronoUnit.HOURS.between(createdAt, currentDateTime);

    if (hoursSinceCreation >= CONFIRM_CODE_EXPIRATION_HOURS) {
      deleteConfirmedCode(emailConfirmationCode.getId());
      throw new MailConfirmationCodeExpiredException("The confirmation code has expired");
    }
  }

  /**
   * Deletes the confirmed {@link EmailConfirmationCode} entity with the specified ID.
   *
   * @param confirmedCodeId The ID of the confirmed email confirmation code to be deleted.
   */
  public void deleteConfirmedCode(long confirmedCodeId) {
    emailConfirmationCodeRepository.deleteById(confirmedCodeId);
  }

}