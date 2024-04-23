package com.ratifire.devrate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.configuration.SecurityConfiguration;
import com.ratifire.devrate.dto.LoginDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.service.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the {@link LoginController}.
 */
@WebMvcTest(LoginController.class)
@Import(SecurityConfiguration.class)
public class LoginControllerTest {

  private static final String END_POINT_PATH = "/auth/signin";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private LoginService loginService;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserDetailsService userDetailsService;

  @Test
  void authenticateUser() throws Exception {
    String testEmail = "test@example.com";
    LoginDto loginDto = LoginDto.builder().email(testEmail).build();
    UserDto expectedUserDto = UserDto.builder().id(123L).build();

    when(loginService.authenticate(any())).thenReturn(expectedUserDto);

    String responseAsString = mockMvc.perform(post(END_POINT_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    UserDto resultUserDto = objectMapper.readValue(responseAsString, UserDto.class);

    assertEquals(expectedUserDto, resultUserDto);
  }
}
