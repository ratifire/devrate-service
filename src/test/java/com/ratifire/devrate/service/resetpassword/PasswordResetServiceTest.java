package com.ratifire.devrate.service.resetpassword;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.PasswordResetDto;
import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.email.EmailService;
import com.ratifire.devrate.service.registration.EmailConfirmationCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Tests for the PasswordResetService class.
 */
@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {

  @Mock
  private UserSecurityService userSecurityService;

  @Mock
  private EmailConfirmationCodeService emailConfirmationCodeService;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private PasswordResetService passwordResetService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Test
  void requestPasswordReset_WithValidEmail_SendsResetCode() {
    String email = "user@example.com";
    UserSecurity userSecurity = UserSecurity.builder().id(1L).build();
    when(userSecurityService.findByEmail(email)).thenReturn(userSecurity);
    when(emailConfirmationCodeService.createConfirmationCode(userSecurity.getId()))
        .thenReturn("code");
    doNothing().when(emailService).sendPasswordResetEmail(any(), any());

    passwordResetService.requestPasswordReset(email);

  }

  @Test
  void resetPassword_WithValidCode_UpdatesPassword() {
    String code = "validCode";
    String newPassword = "newPassword";
    PasswordResetDto passwordResetDto = PasswordResetDto.builder()
        .code(code)
        .newPassword(newPassword)
        .build();
    UserSecurity userSecurity = UserSecurity.builder().id(1L).build();
    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode
        .builder().userSecurityId(userSecurity.getId()).build();

    when(emailConfirmationCodeService.findEmailConfirmationCode(passwordResetDto.getCode()))
        .thenReturn(emailConfirmationCode);
    when(userSecurityService.getById(emailConfirmationCode.getUserSecurityId()))
        .thenReturn(userSecurity);
    doNothing().when(emailConfirmationCodeService)
        .validateAndHandleExpiration(emailConfirmationCode);
    when(passwordEncoder.encode(passwordResetDto.getNewPassword()))
        .thenReturn("encodedPassword");
    when(userSecurityService.save(any())).thenReturn(userSecurity);
    doNothing().when(emailConfirmationCodeService).deleteConfirmedCode(anyLong());
    doNothing().when(emailService).sendPasswordChangeConfirmation(any());

    passwordResetService.resetPassword(passwordResetDto);

    verify(userSecurityService).save(userSecurity);
    verify(emailConfirmationCodeService).validateAndHandleExpiration(emailConfirmationCode);
    verify(passwordEncoder).encode(passwordResetDto.getNewPassword());
    verify(userSecurityService).save(userSecurity);
    verify(emailConfirmationCodeService).deleteConfirmedCode(emailConfirmationCode.getId());
    verify(emailService).sendPasswordChangeConfirmation(userSecurity.getEmail());
  }

}