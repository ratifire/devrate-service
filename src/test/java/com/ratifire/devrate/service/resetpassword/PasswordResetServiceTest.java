package com.ratifire.devrate.service.resetpassword;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.InvalidCodeException;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.email.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Tests for the PasswordResetService class.
 */
@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {

  @Mock
  private UserSecurityService userSecurityService;

  @Mock
  private EmailConfirmationUuidService emailConfirmationUuidService;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private PasswordResetService passwordResetService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Test
  void requestPasswordReset_WithValidEmail_SendsResetLink() {
    String email = "user@example.com";
    UserSecurity userSecurity = UserSecurity.builder().id(1L).build();
    when(userSecurityService.findByEmail(email)).thenReturn(userSecurity);
    when(emailConfirmationUuidService.generateAndPersistUuidCode(userSecurity.getId()))
        .thenReturn("code");
    doNothing().when(emailService).sendPasswordResetEmail(any(), any());

    boolean result = passwordResetService.requestPasswordReset(email);

    assertTrue(result, "Password reset should be requested successfully");
  }

  @Test
  void requestPasswordReset_WithInvalidEmail_ThrowsException() {
    String email = "invalid@example.com";
    when(userSecurityService.findByEmail(email))
        .thenThrow(new UsernameNotFoundException("User not found"));

    assertThrows(UsernameNotFoundException.class, () -> passwordResetService
        .requestPasswordReset(email));
  }

  @Test
  void resetPassword_WithValidCode_UpdatesPassword() {
    String code = "validCode";
    String newPassword = "newPassword";
    UserSecurity userSecurity = UserSecurity.builder().id(1L).build();
    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode
        .builder().userSecurityId(userSecurity.getId()).build();

    when(emailConfirmationUuidService.findUuidCode(code)).thenReturn(emailConfirmationCode);
    when(userSecurityService.getById(userSecurity.getId())).thenReturn(userSecurity);
    when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");
    doNothing().when(emailConfirmationUuidService).deleteConfirmedCodesByUserSecurityId(anyLong());
    doNothing().when(emailService).sendPasswordChangeConfirmation(any());

    boolean result = passwordResetService.resetPassword(code, newPassword);

    assertTrue(result);
    verify(userSecurityService).save(userSecurity);
    verify(passwordEncoder).encode(newPassword);
    verify(emailConfirmationUuidService).deleteConfirmedCodesByUserSecurityId(userSecurity.getId());
  }

  @Test
  void resetPassword_WithInvalidCode_ThrowsException() {
    String code = "invalidCode";
    when(emailConfirmationUuidService.findUuidCode(code))
        .thenThrow(new InvalidCodeException("Invalid or expired password reset code."));

    assertThrows(InvalidCodeException.class, () -> passwordResetService
        .resetPassword(code, "newPassword"));
  }
}