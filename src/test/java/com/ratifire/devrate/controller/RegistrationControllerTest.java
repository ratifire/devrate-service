package com.ratifire.devrate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.UserDto;
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
  private static final String END_POINT_CONFIRM_PATH = "/auth/signup/1/123";
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private RegistrationService registrationService;

  private UserDto userDto;

  /**
   * Initializes the test environment for the RegistrationControllerTest class. This method is
   * executed before each test method in the class.
   */
  @BeforeEach
  public void init() {
    userDto = UserDto.builder()
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
   * Test for {@link RegistrationController#registerUser(UserDto)}.
   *
   * <p>Test method for verifying that the sign-up endpoint ("/signup") returns OK status. This
   * method verifies that the sign-up endpoint returns HTTP status 200 (OK) when accessed.
   *
   * @throws Exception if an error occurs during the test.
   */
  @Test
  public void testSignUpShouldReturnOk() throws Exception {
    UserDto testDto = UserDto.builder().build();

    Mockito.when(registrationService.registerUser(any())).thenReturn(testDto);

    mockMvc.perform(post(END_POINT_PATH)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  public void testConfirmShouldReturnTrue() throws Exception {
    Mockito.when(registrationService.isCodeConfirmed(anyLong(), anyString())).thenReturn(true);

    mockMvc.perform(get(END_POINT_CONFIRM_PATH))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(true));
  }

  @Test
  public void testConfirmShouldReturnFalse() throws Exception {
    Mockito.when(registrationService.isCodeConfirmed(anyLong(), anyString())).thenReturn(false);

    mockMvc.perform(get(END_POINT_CONFIRM_PATH))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(false));
  }
}
