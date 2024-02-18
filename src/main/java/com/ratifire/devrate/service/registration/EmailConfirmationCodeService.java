package com.ratifire.devrate.service.registration;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.exception.EmailConfirmationCodeException;
import com.ratifire.devrate.repository.EmailConfirmationCodeRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Service class responsible for email confirmation logic. This service handles the registration
 * process, including validation and database operations.
 */
@AllArgsConstructor
@Service
public class EmailConfirmationCodeService {

  /**
   * The upper bound for generating random 6 digits code. The constant is set to 999999,
   * representing a 6-digit range.
   */
  private static final int BOUND = 999999;

  private final EmailConfirmationCodeRepository emailConfirmationCodeRepository;

  private final TemplateEngine templateEngine;

  /**
   * Generates a confirmation code, creates an {@code EmailConfirmationCode} object, and saves it
   * for the specified user.
   *
   * @param userId The unique identifier of the user for whom the confirmation code is generated.
   * @return The {@link EmailConfirmationCode} entity saved by the specified user ID.
   */
  public EmailConfirmationCode save(long userId) {
    String confirmationCode = generateConfirmationCode();
    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode.builder()
        .code(confirmationCode)
        .createdAt(LocalDateTime.now())
        .userId(userId)
        .build();

    return emailConfirmationCodeRepository.save(emailConfirmationCode);
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
   * Creates a {@link SimpleMailMessage} for email confirmation.
   *
   * @param userEmail The email address of the user to whom the confirmation code will be sent.
   * @param code      The confirmation code to include in the email.
   * @return A SimpleMailMessage configured with the confirmation email details.
   */
  protected SimpleMailMessage createMessage(String userEmail, String code) {
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setSubject("Your DevRate Confirmation Code - " + code);
    simpleMailMessage.setText(buildConfirmationEmailText(userEmail, code));
    simpleMailMessage.setTo(userEmail);

    return simpleMailMessage;
  }

  /**
   * Build the confirmation email text using Thymeleaf template.
   *
   * @param email The user's email address.
   * @param code  The confirmation code.
   * @return The confirmation email text.
   */
  private String buildConfirmationEmailText(String email, String code) {
    Context context = new Context();
    context.setVariable("email", email);
    context.setVariable("code", code);

    return templateEngine.process("confirmation-email", context);
  }

  /**
   * Retrieves the {@link EmailConfirmationCode} entity for a given user ID.
   *
   * @param userId The ID of the user for whom to retrieve the email confirmation code.
   * @return The {@link EmailConfirmationCode} entity associated with the specified user ID.
   * @throws EmailConfirmationCodeException If the confirmation code for the user ID is not found.
   */
  public EmailConfirmationCode getEmailConfirmationCodeByUserId(long userId) {
    return emailConfirmationCodeRepository.findByUserId(userId)
        .orElseThrow(() -> new EmailConfirmationCodeException("The confirmation code for the "
            + "user ID \"" + userId + "\" could not be found."));
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
