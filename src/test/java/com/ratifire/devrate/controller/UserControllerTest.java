package com.ratifire.devrate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.configuration.SecurityConfiguration;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for the {@link UserController} class.
 */
@WebMvcTest(UserController.class)
@Import(SecurityConfiguration.class)
class UserControllerTest {

  private static final long USER_ID = 1L;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @MockBean
  private UserDetailsService userDetailsService;

  private UserDto userDto;

  @BeforeEach
  public void setUp() {
    userDto = UserDto.builder()
        .id(USER_ID)
        .firstName("firstName")
        .lastName("lastName")
        .position("position")
        .country("country")
        .region("region")
        .city("city")
        .subscribed(true)
        .description("description")
        .build();
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void findByIdTest() throws Exception {
    when(userService.findById(USER_ID)).thenReturn(userDto);
    mockMvc.perform(get("/users/{id}", USER_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(userDto.getId()))
        .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(userDto.getLastName()))
        .andExpect(jsonPath("$.position").value(userDto.getPosition()))
        .andExpect(jsonPath("$.country").value(userDto.getCountry()))
        .andExpect(jsonPath("$.region").value(userDto.getRegion()))
        .andExpect(jsonPath("$.city").value(userDto.getCity()))
        .andExpect(jsonPath("$.subscribed").value(userDto.isSubscribed()))
        .andExpect(jsonPath("$.description").value(userDto.getDescription()));

    verify(userService, times(1)).findById(anyLong());
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void updateTest() throws Exception {
    String requestBody = objectMapper.writeValueAsString(userDto);
    when(userService.update(any(UserDto.class))).thenReturn(userDto);
    mockMvc.perform(put("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(userDto.getId()))
        .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(userDto.getLastName()))
        .andExpect(jsonPath("$.position").value(userDto.getPosition()))
        .andExpect(jsonPath("$.country").value(userDto.getCountry()))
        .andExpect(jsonPath("$.region").value(userDto.getRegion()))
        .andExpect(jsonPath("$.city").value(userDto.getCity()))
        .andExpect(jsonPath("$.subscribed").value(userDto.isSubscribed()))
        .andExpect(jsonPath("$.description").value(userDto.getDescription()));

    verify(userService, times(1)).update(any(UserDto.class));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void deleteTest() throws Exception {
    mockMvc.perform(delete("/users/{id}", USER_ID))
        .andExpect(status().isOk());
  }
}