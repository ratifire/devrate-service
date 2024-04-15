package com.ratifire.devrate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.configuration.SecurityConfiguration;
import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.enums.LanguageProficiencyLevel;
import com.ratifire.devrate.enums.LanguageProficiencyName;
import com.ratifire.devrate.service.LanguageProficiencyService;
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
 * Unit tests for the LanguageProficiencyController class.
 */
@WebMvcTest(LanguageProficiencyController.class)
@Import(SecurityConfiguration.class)
class LanguageProficiencyControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private LanguageProficiencyService languageProficiencyService;

  @MockBean
  private UserDetailsService userDetailsService;

  @Autowired
  private ObjectMapper objectMapper;
  private final long languageProficiencyId = 1;
  private LanguageProficiencyDto languageProficiencyDto;
  private String content;

  @BeforeEach
  public void setUp() throws Exception {
    languageProficiencyDto = LanguageProficiencyDto.builder()
        .id(languageProficiencyId)
        .name(LanguageProficiencyName.ENGLISH)
        .level(LanguageProficiencyLevel.INTERMEDIATE_B1)
        .build();

    content = objectMapper.writeValueAsString(languageProficiencyDto);
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void findByIdTest() throws Exception {
    when(languageProficiencyService.findById(languageProficiencyId)).thenReturn(
        languageProficiencyDto);
    mockMvc.perform(get("/language-proficiencies/{id}", languageProficiencyId))
        .andExpect(status().isOk())
        .andExpect(content().json(content));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void updateTest() throws Exception {
    when(
        languageProficiencyService.update(anyLong(), any(LanguageProficiencyDto.class)))
        .thenReturn(languageProficiencyDto);
    mockMvc.perform(put("/language-proficiencies/{id}", languageProficiencyId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(status().isOk())
        .andExpect(content().json(content));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void deleteTest() throws Exception {
    mockMvc.perform(delete("/language-proficiencies/{id}", languageProficiencyId))
        .andExpect(status().isOk());
  }
}