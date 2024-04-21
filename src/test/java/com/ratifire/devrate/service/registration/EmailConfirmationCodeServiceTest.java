package com.ratifire.devrate.service.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.exception.MailConfirmationCodeException;
import com.ratifire.devrate.repository.EmailConfirmationCodeRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the EmailConfirmationCodeService class.
 */
@ExtendWith(MockitoExtension.class)
class EmailConfirmationCodeServiceTest {

  @Mock
  private EmailConfirmationCodeRepository emailConfirmationCodeRepository;

  @InjectMocks
  private EmailConfirmationCodeService emailConfirmationCodeService;

  @Test
  void testGenerateConfirmationCodeFormat() {
    long userId = 1L;
    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode.builder().build();

    when(emailConfirmationCodeRepository.save(any())).thenReturn(emailConfirmationCode);

    String actualConfirmationCode = emailConfirmationCodeService.getConfirmationCode(userId);

    assertTrue(actualConfirmationCode.matches("\\d{6}"),
        "Confirmation code should be 6 digits long");
  }

  @Test
  void testGenerateUniqueConfirmationCodes() {
    long userId = 1L;
    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode.builder().build();

    when(emailConfirmationCodeRepository.save(any())).thenReturn(emailConfirmationCode);

    String confirmationCode1 = emailConfirmationCodeService.getConfirmationCode(userId);
    String confirmationCode2 = emailConfirmationCodeService.getConfirmationCode(userId);

    assertNotEquals(confirmationCode1, confirmationCode2,
        "Generated confirmation codes should be unique");
  }

  @Test
  void testFindEmailConfirmationCode_Success() {
    long userId = 1L;
    String confirmationCode = "123456";
    EmailConfirmationCode expectedCode = EmailConfirmationCode.builder()
        .code(confirmationCode)
        .createdAt(LocalDateTime.now())
        .userSecurityId(userId)
        .build();

    when(emailConfirmationCodeRepository.findByCode(confirmationCode))
        .thenReturn(Optional.of(expectedCode));

    EmailConfirmationCode actualCode =
        emailConfirmationCodeService.findEmailConfirmationCode(confirmationCode);

    assertEquals(expectedCode, actualCode);
  }

  @Test
  void testFindEmailConfirmationCode_CodeNotFound_ShouldThrowEmailConfirmationCodeException() {
    String confirmationCode = "123456";

    when(emailConfirmationCodeRepository.findByCode(confirmationCode))
        .thenReturn(Optional.empty());

    assertThrows(MailConfirmationCodeException.class,
        () -> emailConfirmationCodeService.findEmailConfirmationCode(confirmationCode));
  }

  @Test
  void testDeleteConfirmedCode() {
    long confirmedCodeId = 1L;

    emailConfirmationCodeService.deleteConfirmedCode(confirmedCodeId);

    verify(emailConfirmationCodeRepository, times(1))
        .deleteById(confirmedCodeId);
  }
}