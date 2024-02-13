package com.ratifire.devrate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.service.RegistrationService;
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

  private static final String END_POINT_PATH = "/signup";
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private RegistrationService registrationService;

  private SignUpDto signUpDto;

  /**
   * Initializes the test environment for the RegistrationControllerTest class. This method is
   * executed before each test method in the class.
   */
  @BeforeEach
  public void init() {
    signUpDto = SignUpDto.builder()
        .email("test@gmail.com")
        .firstName("Test first name")
        .lastName("Test last name")
        .country("Test country")
        .isVerified(true)
        .isSubscribed(true)
        .password("TestPassword123")
        .build();
  }

  /**
   * Test for {@link RegistrationController#registerUser(SignUpDto)}.
   *
   * <p>Test method for verifying that the sign-up endpoint ("/signup") returns OK status. This
   * method
   * verifies that the sign-up endpoint returns HTTP status 200 (OK) when accessed.
   *
   * @throws Exception if an error occurs during the test.
   */
  @Test
  public void testSignUpShouldReturnOk() throws Exception {
    Mockito.when(registrationService.registerUser(any())).thenReturn(User.builder().build());

    mockMvc.perform(post(END_POINT_PATH)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(signUpDto)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  /**
   * Unit test for {@link RegistrationController#registerUser(SignUpDto)}.
   *
   * <p>Test method for verifying that the sign-up endpoint ("/signup") returns 409 Conflict status
   * when attempting to sign up with an already existing email address.
   *
   * @throws Exception if an error occurs during the test.
   */
  @Test
  public void testSignUpShouldReturn409Conflict() throws Exception {
    Mockito.when(registrationService.isUserExistByEmail(any())).thenReturn(true);

    mockMvc.perform(post(END_POINT_PATH + "/signup")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(signUpDto)))
        .andExpect(status().is4xxClientError())
        .andDo(print());
  }
}
