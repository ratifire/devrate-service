package com.ratifire.devrate.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ratifire.devrate.exception.InvalidCodeException;
import com.ratifire.devrate.service.resetpassword.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the {@link PasswordResetController}.
 *
 * <p>This class tests the behavior of the PasswordResetController endpoints.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PasswordResetControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PasswordResetService passwordResetService;

  /**
   * Tests that a request to reset password returns an OK status when successful.
   *
   * @throws Exception if an error occurs during the test execution.
   */
  @Test
  public void requestPasswordResetShouldReturnSuccessMessage() throws Exception {
    String email = "user@example.com";

    when(passwordResetService.requestPasswordReset(anyString())).thenReturn(true);

    mockMvc.perform(post("/api/auth/password-reset/request")
            .param("email", email))
        .andExpect(status().isOk());
  }

  /**
   * Tests that resetting password with a valid code returns true.
   *
   * @throws Exception if an error occurs during the test execution.
   */
  @Test
  public void resetPasswordWithValidCodeShouldReturnTrue() throws Exception {
    String code = "valid-code";
    String newPassword = "{\"newPassword\":\"newStrongPassword123\"}";

    when(passwordResetService.resetPassword(eq(code), anyString())).thenReturn(true);

    mockMvc.perform(post("/api/auth/password-reset/{code}", code)
            .contentType("application/json")
            .content(newPassword))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  /**
   * Tests that resetting password with an invalid code returns an error message.
   *
   * @throws Exception if an error occurs during the test execution.
   */
  @Test
  public void resetPasswordWithInvalidCodeShouldReturnError() throws Exception {
    String invalidCode = "invalid-code";
    String newPassword = "{\"newPassword\":\"newStrongPassword123\"}";

    doThrow(new InvalidCodeException("Invalid or expired password reset code."))
        .when(passwordResetService).resetPassword(eq(invalidCode), anyString());

    mockMvc.perform(post("/api/auth/password-reset/{code}", invalidCode)
            .contentType("application/json")
            .content(newPassword))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid or expired password reset code.")));
  }
}
