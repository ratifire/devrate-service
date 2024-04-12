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

  private long languageProficiencyId = 1;

  private long userId = 1;

  private LanguageProficiencyDto languageProficiencyDto;

  private LanguageProficiencyDto languageProficiencyDtoUpdated;

  @BeforeEach
  public void setUp() {
    languageProficiencyDto = LanguageProficiencyDto.builder()
        .id(languageProficiencyId)
        .name(LanguageProficiencyName.ENGLISH)
        .level(LanguageProficiencyLevel.INTERMEDIATE_B1)
        .build();

    languageProficiencyDtoUpdated = LanguageProficiencyDto.builder()
        .id(languageProficiencyId)
        .name(LanguageProficiencyName.ENGLISH)
        .level(LanguageProficiencyLevel.UPPER_INTERMEDIATE_B2)
        .build();

  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void findByIdTest() throws Exception {
    when(languageProficiencyService.findById(languageProficiencyId)).thenReturn(
        languageProficiencyDto);
    mockMvc.perform(get("/language-proficiencies/{id}", languageProficiencyId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(languageProficiencyDto.getName().getCode()))
        .andExpect(jsonPath("$.level").value(languageProficiencyDto.getLevel()
            .toString()));
    verify(languageProficiencyService, times(1)).findById(anyLong());
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void updateTest() throws Exception {
    String requestBody = objectMapper.writeValueAsString(languageProficiencyDtoUpdated);
    when(
        languageProficiencyService.update(anyLong(), any(LanguageProficiencyDto.class)))
        .thenReturn(languageProficiencyDtoUpdated);
    mockMvc.perform(put("/language-proficiencies/{id}", languageProficiencyId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(languageProficiencyDtoUpdated.getName()
            .getCode()))
        .andExpect(jsonPath("$.level").value(languageProficiencyDtoUpdated.getLevel()
            .toString()));
    verify(languageProficiencyService, times(1)).update(anyLong(),
        any(LanguageProficiencyDto.class));

  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void deleteTest() throws Exception {
    mockMvc.perform(delete("/language-proficiencies/{id}", languageProficiencyId))
        .andExpect(status().isOk());
  }

}