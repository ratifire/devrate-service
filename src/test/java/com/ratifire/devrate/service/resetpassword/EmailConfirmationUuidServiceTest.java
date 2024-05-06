package com.ratifire.devrate.service.resetpassword;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.EmailConfirmationCode;
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
 * Tests for {@link EmailConfirmationUuidService}.
 */
@ExtendWith(SpringExtension.class)
public class EmailConfirmationUuidServiceTest {

  @Mock
  private EmailConfirmationCodeRepository emailConfirmationCodeRepository;

  @InjectMocks
  private EmailConfirmationUuidService emailConfirmationUuidService;

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
}

