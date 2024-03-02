package com.ratifire.devrate.service.resetpassword;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.exception.InvalidCodeException;
import com.ratifire.devrate.repository.EmailConfirmationCodeRepository;
import com.ratifire.devrate.service.email.EmailService;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Tests for {@link EmailConfirmationUuidService}.
 * Includes tests for generating and persisting UUID codes, finding UUID codes,
 * sending password reset and change confirmation emails, and deleting confirmed codes.
 */
@ExtendWith(SpringExtension.class)
public class EmailConfirmationUuidServiceTest {

  @Mock
  private EmailConfirmationCodeRepository emailConfirmationCodeRepository;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private EmailConfirmationUuidService emailConfirmationUuidService;

  /**
   * Tests generating and persisting a UUID code for a given user ID.
   */
  @Test
  public void generateAndPersistUuidCode_GeneratesAndSavesCode() {
    Long userId = 1L;
    when(emailConfirmationCodeRepository.save(any(EmailConfirmationCode.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    String code = emailConfirmationUuidService.generateAndPersistUuidCode(userId);

    assertNotNull(code, "Generated code should not be null");
    verify(emailConfirmationCodeRepository).deleteByUserId(userId);
    verify(emailConfirmationCodeRepository).save(any(EmailConfirmationCode.class));
  }

  /**
   * Tests finding a UUID code when the code is valid.
   */
  @Test
  public void findUuidCode_WithValidCode_ReturnsCode() {
    String validCode = UUID.randomUUID().toString();
    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode.builder()
        .code(validCode)
        .userId(1L)
        .createdAt(LocalDateTime.now().plusHours(12))
        .build();

    when(emailConfirmationCodeRepository.findByCode(validCode))
        .thenReturn(Optional.of(emailConfirmationCode));

    EmailConfirmationCode result = emailConfirmationUuidService.findUuidCode(validCode);

    assertEquals(validCode, result.getCode(), "Should return the correct code");
  }

  /**
   * Tests behavior when a UUID code is invalid.
   */
  @Test
  public void findUuidCode_WithInvalidCode_ThrowsInvalidCodeException() {
    String invalidCode = "invalid_code";
    when(emailConfirmationCodeRepository.findByCode(invalidCode))
        .thenReturn(Optional.empty());

    assertThrows(InvalidCodeException.class,
        () -> emailConfirmationUuidService.findUuidCode(invalidCode),
        "Should throw InvalidCodeException for an invalid code");
  }

  /**
   * Tests sending a password reset email.
   */
  @Test
  public void sendPasswordResetEmail_SendsEmailWithResetLink() {
    String userEmail = "test@example.com";
    String code = UUID.randomUUID().toString();

    emailConfirmationUuidService.sendPasswordResetEmail(userEmail, code);

    verify(emailService).sendEmail(argThat(mailMessage ->
        Objects.requireNonNull(mailMessage.getTo())[0].equals(userEmail)
            && "Password Reset".equals(mailMessage.getSubject())
            && Objects.requireNonNull(mailMessage.getText())
            .contains("#" + code)), eq(false));
  }

  /**
   * Tests sending a password change confirmation email.
   */
  @Test
  public void sendPasswordChangeConfirmation_SendsConfirmationEmail() {
    String userEmail = "test@example.com";

    emailConfirmationUuidService.sendPasswordChangeConfirmation(userEmail);

    verify(emailService).sendEmail(argThat(mailMessage ->
        Objects.requireNonNull(mailMessage.getTo())[0].equals(userEmail)
            && "Password Successfully Reset".equals(mailMessage.getSubject())), eq(false));
  }

  /**
   * Tests deleting confirmed codes by user ID.
   */
  @Test
  public void deleteConfirmedCodesByUserId_DeletesCodes() {
    Long userId = 1L;
    emailConfirmationUuidService.deleteConfirmedCodesByUserId(userId);
    verify(emailConfirmationCodeRepository).deleteByUserId(userId);
  }

  /**
   * Tests creating a unique UUID.
   */
  @Test
  public void createUniqueUuid_CreatesValidUuid() {
    String uuid = emailConfirmationUuidService.createUniqueUuid();
    assertNotNull(uuid, "UUID should not be null");
    assertTrue(uuid.matches("[a-f0-9\\-]{36}"), "UUID should match the UUID format");
  }
}
