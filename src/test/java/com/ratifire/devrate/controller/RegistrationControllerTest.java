package com.ratifire.devrate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.UserRegistrationDto;
import com.ratifire.devrate.exception.EmailConfirmationCodeException;
import com.ratifire.devrate.exception.EmailConfirmationCodeRequestException;
import com.ratifire.devrate.service.registration.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the {@link RegistrationController}.
 *
 * <p>This class tests the behavior of the RegistrationController endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {

  private static final String END_POINT_PATH = "/auth/signup";
  private static final String END_POINT_CONFIRM_PATH = "/auth/signup/123";
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private RegistrationService registrationService;

  private UserRegistrationDto userRegistrationDto;

  /**
   * Initializes the test environment for the RegistrationControllerTest class. This method is
   * executed before each test method in the class.
   */
  @BeforeEach
  public void init() {
    userRegistrationDto = UserRegistrationDto.builder()
        .email("test@gmail.com")
        .firstName("Test first name")
        .lastName("Test last name")
        .country("Test country")
        .verified(true)
        .subscribed(true)
        .password("TestPassword123")
        .build();
  }

  /**
   * Test for {@link RegistrationController#registerUser(UserRegistrationDto)}.
   *
   * <p>Test method for verifying that the sign-up endpoint ("/signup") returns OK status. This
   * method verifies that the sign-up endpoint returns HTTP status 200 (OK) when accessed.
   *
   * @throws Exception if an error occurs during the test.
   */
  @Test
  public void testSignUpShouldReturnOk() throws Exception {
    Mockito.doNothing().when(registrationService).registerUser(any());

    mockMvc.perform(post(END_POINT_PATH)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(userRegistrationDto)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  /**
   * Test for {@link RegistrationController#confirm(String)}.
   *
   * <p>Test method for verifying that the sign-up endpoint ("/signup") returns OK status. This
   * method verifies that the sign-up endpoint returns HTTP status 200 (OK) when accessed.
   *
   * @throws Exception if an error occurs during the test.
   */
  @Test
  public void testConfirmShouldReturnUserId() throws Exception {
    Mockito.when(registrationService.confirmRegistration(anyString())).thenReturn(1L);

    mockMvc.perform(put(END_POINT_CONFIRM_PATH))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").value(1L));
  }

  /**
   * Test for {@link RegistrationController#confirm(String)}.
   *
   * <p>This test ensures that the {@code confirmRegistration} method in {@code RegistrationService}
   * throws {@code EmailConfirmationCodeException}, and the controller returns HTTP status code 404
   * (NOT FOUND) in response.
   *
   * @throws Exception if an error occurs during the test execution
   */
  @Test
  public void testConfirm_InvalidCode_ShouldReturnStatusCode404() throws Exception {
    Mockito.when(registrationService.confirmRegistration(anyString()))
        .thenThrow(EmailConfirmationCodeException.class);

    mockMvc.perform(put(END_POINT_CONFIRM_PATH))
        .andExpect(status().isNotFound());
  }

  /**
   * Test for {@link RegistrationController#confirm(String)}.
   *
   * <p>This test ensures that the {@code confirmRegistration} method in {@code RegistrationService}
   * throws {@code EmailConfirmationCodeRequestException}, and the controller returns HTTP status
   * code 400 (BAD REQUEST) in response.
   *
   * @throws Exception if an error occurs during the test execution
   */
  @Test
  public void testConfirm_InvalidRequest_ShouldReturnStatusCode400() throws Exception {
    Mockito.when(registrationService.confirmRegistration(anyString()))
        .thenThrow(EmailConfirmationCodeRequestException.class);

    mockMvc.perform(put(END_POINT_CONFIRM_PATH))
        .andExpect(status().isBadRequest());
  }
}
