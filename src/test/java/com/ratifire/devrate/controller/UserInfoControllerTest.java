package com.ratifire.devrate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.configuration.SecurityConfiguration;
import com.ratifire.devrate.dto.UserInfoDto;
import com.ratifire.devrate.service.userinfo.UserInfoService;
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
 * Unit tests for the {@link UserInfoController} class.
 */
@WebMvcTest(UserInfoController.class)
@Import(SecurityConfiguration.class)
class UserInfoControllerTest {

  private static final long USER_ID = 1L;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserInfoService userInfoService;

  @MockBean
  private UserDetailsService userDetailsService;

  private UserInfoDto userInfoDto;

  @BeforeEach
  public void setUp() {
    userInfoDto = UserInfoDto.builder()
        .firstName("firstName")
        .lastName("lastName")
        .position("position")
        .country("country")
        .state("state")
        .city("city")
        .subscribed(true)
        .description("description")
        .userId(USER_ID)
        .build();
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void findByUserIdTest() throws Exception {
    when(userInfoService.findByUserId(USER_ID)).thenReturn(userInfoDto);
    mockMvc.perform(get("/user/{userId}", USER_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.firstName").value(userInfoDto.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(userInfoDto.getLastName()))
        .andExpect(jsonPath("$.position").value(userInfoDto.getPosition()))
        .andExpect(jsonPath("$.country").value(userInfoDto.getCountry()))
        .andExpect(jsonPath("$.state").value(userInfoDto.getState()))
        .andExpect(jsonPath("$.city").value(userInfoDto.getCity()))
        .andExpect(jsonPath("$.subscribed").value(userInfoDto.isSubscribed()))
        .andExpect(jsonPath("$.description").value(userInfoDto.getDescription()))
        .andExpect(jsonPath("$.userId").value(userInfoDto.getUserId()));

    verify(userInfoService, times(1)).findByUserId(anyLong());
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void createTest() throws Exception {
    String requestBody = objectMapper.writeValueAsString(userInfoDto);
    when(userInfoService.create(any(UserInfoDto.class))).thenReturn(userInfoDto);
    mockMvc.perform(post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.firstName").value(userInfoDto.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(userInfoDto.getLastName()))
        .andExpect(jsonPath("$.position").value(userInfoDto.getPosition()))
        .andExpect(jsonPath("$.country").value(userInfoDto.getCountry()))
        .andExpect(jsonPath("$.state").value(userInfoDto.getState()))
        .andExpect(jsonPath("$.city").value(userInfoDto.getCity()))
        .andExpect(jsonPath("$.subscribed").value(userInfoDto.isSubscribed()))
        .andExpect(jsonPath("$.description").value(userInfoDto.getDescription()))
        .andExpect(jsonPath("$.userId").value(userInfoDto.getUserId()));

    verify(userInfoService, times(1)).create(any(UserInfoDto.class));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void updateTest() throws Exception {
    String requestBody = objectMapper.writeValueAsString(userInfoDto);
    when(userInfoService.update(any(UserInfoDto.class))).thenReturn(userInfoDto);
    mockMvc.perform(put("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.firstName").value(userInfoDto.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(userInfoDto.getLastName()))
        .andExpect(jsonPath("$.position").value(userInfoDto.getPosition()))
        .andExpect(jsonPath("$.country").value(userInfoDto.getCountry()))
        .andExpect(jsonPath("$.state").value(userInfoDto.getState()))
        .andExpect(jsonPath("$.city").value(userInfoDto.getCity()))
        .andExpect(jsonPath("$.subscribed").value(userInfoDto.isSubscribed()))
        .andExpect(jsonPath("$.description").value(userInfoDto.getDescription()))
        .andExpect(jsonPath("$.userId").value(userInfoDto.getUserId()));

    verify(userInfoService, times(1)).update(any(UserInfoDto.class));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void deleteTest() throws Exception {
    mockMvc.perform(delete("/user/{userId}", USER_ID))
        .andExpect(status().isOk());
  }
}