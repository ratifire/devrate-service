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
import com.ratifire.devrate.dto.LanguageDto;
import com.ratifire.devrate.enums.LanguageLevel;
import com.ratifire.devrate.enums.LanguageName;
import com.ratifire.devrate.service.LanguageService;
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
 * Unit tests for the LanguageController class.
 */
@WebMvcTest(LanguageController.class)
@Import(SecurityConfiguration.class)
class LanguageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private LanguageService languageService;

  @MockBean
  private UserDetailsService userDetailsService;

  @Autowired
  private ObjectMapper objectMapper;

  private long languageId = 1;

  private long userId = 1;

  private LanguageDto languageDto;

  private LanguageDto languageDtoUpdated;

  @BeforeEach
  public void setUp() {
    languageDto = LanguageDto.builder()
        .id(languageId)
        .name(LanguageName.EN)
        .level(LanguageLevel.INTERMEDIATE_B1)
        .build();

    languageDtoUpdated = LanguageDto.builder()
        .id(languageId)
        .name(LanguageName.EN)
        .level(LanguageLevel.UPPER_INTERMEDIATE_B2)
        .build();

  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void findByIdTest() throws Exception {
    when(languageService.findById(languageId)).thenReturn(languageDto);
    mockMvc.perform(get("/languages/{id}", languageId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(languageDto.getName().toString()))
        .andExpect(jsonPath("$.level").value(languageDto.getLevel().toString()));
    verify(languageService, times(1)).findById(anyLong());
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void createTest() throws Exception {
    String requestBody = objectMapper.writeValueAsString(languageDto);
    when(languageService.create(anyLong(), any(LanguageDto.class))).thenReturn(languageDto);
    mockMvc.perform(post("/languages/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(languageDto.getName().toString()))
        .andExpect(jsonPath("$.level").value(languageDto.getLevel().toString()));
    verify(languageService, times(1)).create(anyLong(), any(LanguageDto.class));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void updateTest() throws Exception {
    String requestBody = objectMapper.writeValueAsString(languageDtoUpdated);
    when(languageService.update(anyLong(), any(LanguageDto.class))).thenReturn(languageDtoUpdated);
    mockMvc.perform(put("/languages/{id}", languageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(languageDtoUpdated.getName().toString()))
        .andExpect(jsonPath("$.level").value(languageDtoUpdated.getLevel().toString()));
    verify(languageService, times(1)).update(anyLong(), any(LanguageDto.class));

  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void deleteTest() throws Exception {
    mockMvc.perform(delete("/languages/{id}", languageId))
        .andExpect(status().isOk());
  }

}