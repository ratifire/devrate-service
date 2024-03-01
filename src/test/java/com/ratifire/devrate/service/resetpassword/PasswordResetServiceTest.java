package com.ratifire.devrate.service.resetpassword;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.InvalidCodeException;
import com.ratifire.devrate.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Tests for the PasswordResetService class. Verifies behavior for password reset requests,
 * including sending a reset link with a valid email, throwing exceptions for invalid emails,
 * successfully updating the password with a valid code, and handling invalid codes.
 * Utilizes Mockito to mock dependencies for isolated testing.
 */
@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {

  @Mock
  private UserService userService;

  @Mock
  private EmailConfirmationUuidService emailConfirmationUuidService;

  @InjectMocks
  private PasswordResetService passwordResetService;

  @Mock
  private PasswordEncoder passwordEncoder;

  /**
   * Tests that a password reset request successfully sends a reset link
   * when the user's email is valid.
   */
  @Test
  void requestPasswordReset_WithValidEmail_SendsResetLink() {
    String email = "user@example.com";
    User user = User.builder().id(1L).build();
    when(userService.findUserByEmail(email)).thenReturn(user);
    when(emailConfirmationUuidService.generateAndPersistUuidCode(user.getId())).thenReturn("code");

    boolean result = passwordResetService.requestPasswordReset(email);

    assertTrue(result, "Password reset should be requested successfully");
    verify(emailConfirmationUuidService).sendPasswordResetEmail(email, "code");
  }

  /**
   * Tests that a UserNotFoundException is thrown when attempting to reset
   * a password with an invalid email.
   */
  @Test
  void requestPasswordReset_WithInvalidEmail_ThrowsException() {
    String email = "invalid@example.com";
    when(userService.findUserByEmail(email))
        .thenThrow(new UsernameNotFoundException("User not found"));

    assertThrows(UsernameNotFoundException.class, () -> passwordResetService
        .requestPasswordReset(email));
  }

  /**
   * Tests that a password is successfully reset when a valid code is provided.
   */
  @Test
  void resetPassword_WithValidCode_UpdatesPassword() {
    String code = "validCode";
    String newPassword = "newPassword";
    User user = User.builder().id(1L).build();
    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode
        .builder().userId(user.getId()).build();

    when(emailConfirmationUuidService.findUuidCode(code)).thenReturn(emailConfirmationCode);
    when(userService.getById(user.getId())).thenReturn(user);
    when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");

    boolean result = passwordResetService.resetPassword(code, newPassword);

    assertTrue(result);
    verify(userService).save(user);
    verify(passwordEncoder).encode(newPassword);
    verify(emailConfirmationUuidService).deleteConfirmedCodesByUserId(user.getId());
  }


  /**
   * Tests that an InvalidCodeException is thrown when attempting to reset
   * a password with an invalid code.
   */
  @Test
  void resetPassword_WithInvalidCode_ThrowsException() {
    String code = "invalidCode";
    when(emailConfirmationUuidService.findUuidCode(code))
        .thenThrow(new InvalidCodeException("Invalid or expired password reset code."));

    assertThrows(InvalidCodeException.class, () -> passwordResetService
        .resetPassword(code, "newPassword"));
  }
}