package com.ratifire.devrate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.LoginDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.service.UserSecurityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the {@link LoginController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

  private static final String END_POINT_PATH = "/auth/signin";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserSecurityService userSecurityService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void authenticateUser() throws Exception {
    String testEmail = "test@example.com";
    LoginDto loginDto = LoginDto.builder().email(testEmail).build();
    String requestBody = objectMapper.writeValueAsString(loginDto);
    UserDto expectedUserDto = UserDto.builder()
        .id(123L)
        .build();

    when(userSecurityService.authenticate(any())).thenReturn(expectedUserDto);

    UserDto resultUserDto = objectMapper.readValue(
        mockMvc.perform(post(END_POINT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(), UserDto.class);

    assertEquals(expectedUserDto.getId(), resultUserDto.getId());
  }
}
