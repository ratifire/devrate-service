package com.ratifire.devrate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.configuration.SecurityConfiguration;
import com.ratifire.devrate.dto.UserRegistrationDto;
import com.ratifire.devrate.exception.MailConfirmationCodeException;
import com.ratifire.devrate.exception.MailConfirmationCodeRequestException;
import com.ratifire.devrate.service.registration.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the {@link RegistrationController}.
 *
 * <p>This class tests the behavior of the RegistrationController endpoints.
 */
@WebMvcTest(RegistrationController.class)
@Import(SecurityConfiguration.class)
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

  @MockBean
  private UserDetailsService userDetailsService;

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

  @Test
  public void testSignUpShouldReturnOk() throws Exception {
    Mockito.doNothing().when(registrationService).registerUser(any());

    mockMvc.perform(post(END_POINT_PATH)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(userRegistrationDto)))
        .andExpect(status().isOk());
  }

  @Test
  public void testConfirmShouldReturnUserId() throws Exception {
    Mockito.when(registrationService.confirmRegistration(anyString())).thenReturn(1L);

    mockMvc.perform(put(END_POINT_CONFIRM_PATH))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").value(1L));
  }

  @Test
  public void testConfirm_InvalidCode_ShouldReturnStatusCode404() throws Exception {
    Mockito.when(registrationService.confirmRegistration(anyString()))
        .thenThrow(new MailConfirmationCodeException("Code not found"));

    mockMvc.perform(put(END_POINT_CONFIRM_PATH))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testConfirm_InvalidRequest_ShouldReturnStatusCode400() throws Exception {
    Mockito.when(registrationService.confirmRegistration(anyString()))
        .thenThrow(new MailConfirmationCodeRequestException("Code is a required argument"));

    mockMvc.perform(put(END_POINT_CONFIRM_PATH))
        .andExpect(status().isBadRequest());
  }
}
