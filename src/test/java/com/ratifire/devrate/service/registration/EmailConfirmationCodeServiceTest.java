package com.ratifire.devrate.service.registration;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.repository.EmailConfirmationCodeRepository;
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
}