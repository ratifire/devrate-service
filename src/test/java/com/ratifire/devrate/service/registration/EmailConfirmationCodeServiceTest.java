package com.ratifire.devrate.service.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.exception.EmailConfirmationCodeException;
import com.ratifire.devrate.repository.EmailConfirmationCodeRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailConfirmationCodeServiceTest {

  @Mock
  private EmailConfirmationCodeRepository emailConfirmationCodeRepository;

  @InjectMocks
  private EmailConfirmationCodeService emailConfirmationCodeService;

  @Test
  void testSave_ShouldReturnEmailConfirmationCode() {
    long userId = 1L;

    EmailConfirmationCode expectedConfirmationCode = EmailConfirmationCode.builder()
        .code("123456")
        .createdAt(LocalDateTime.now())
        .userId(userId)
        .build();

    when(emailConfirmationCodeRepository.save(any(EmailConfirmationCode.class)))
        .thenReturn(expectedConfirmationCode);

    EmailConfirmationCode actualConfirmationCode = emailConfirmationCodeService.save(userId);

    assertEquals(expectedConfirmationCode, actualConfirmationCode);
  }

  @Test
  void testGenerateConfirmationCode() {
    String code = emailConfirmationCodeService.generateConfirmationCode();

    assertTrue(code.matches("\\d{6}"));
  }

  @Test
  void testGetEmailConfirmationCodeByUserId() {
    long userId = 1L;
    EmailConfirmationCode expectedCode = EmailConfirmationCode.builder()
        .code("123456")
        .createdAt(LocalDateTime.now())
        .userId(userId)
        .build();

    when(emailConfirmationCodeRepository.findByUserId(userId))
        .thenReturn(Optional.of(expectedCode));

    EmailConfirmationCode actualCode =
        emailConfirmationCodeService.getEmailConfirmationCodeByUserId(userId);

    assertEquals(expectedCode, actualCode);
  }

  @Test
  void testGetEmailConfirmationCodeByUserIdNotFound_ShouldThrowEmailConfirmationCodeException() {
    long userId = 1L;

    when(emailConfirmationCodeRepository.findByUserId(userId)).thenReturn(Optional.empty());

    assertThrows(EmailConfirmationCodeException.class,
        () -> emailConfirmationCodeService.getEmailConfirmationCodeByUserId(userId));
  }

  @Test
  void testDeleteConfirmedCode() {
    long confirmedCodeId = 1L;

    emailConfirmationCodeService.deleteConfirmedCode(confirmedCodeId);

    verify(emailConfirmationCodeRepository, times(1))
        .deleteById(confirmedCodeId);
  }
}