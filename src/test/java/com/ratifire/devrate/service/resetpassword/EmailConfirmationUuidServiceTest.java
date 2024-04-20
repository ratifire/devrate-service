package com.ratifire.devrate.service.resetpassword;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.exception.InvalidCodeException;
import com.ratifire.devrate.repository.EmailConfirmationCodeRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Tests for {@link EmailConfirmationUuidService}. Includes tests for generating and persisting UUID
 * codes, finding UUID codes, sending password reset and change confirmation emails, and deleting
 * confirmed codes.
 */
@ExtendWith(SpringExtension.class)
public class EmailConfirmationUuidServiceTest {

  @Mock
  private EmailConfirmationCodeRepository emailConfirmationCodeRepository;

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
    verify(emailConfirmationCodeRepository).deleteByUserSecurityId(userId);
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
        .userSecurityId(1L)
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
   * Tests deleting confirmed codes by user ID.
   */
  @Test
  public void deleteConfirmedCodesByUserId_DeletesCodes() {
    Long userId = 1L;
    emailConfirmationUuidService.deleteConfirmedCodesByUserSecurityId(userId);
    verify(emailConfirmationCodeRepository).deleteByUserSecurityId(userId);
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

