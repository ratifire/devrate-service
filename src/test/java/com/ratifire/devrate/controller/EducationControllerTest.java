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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.service.EducationService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EducationController.class)
public class EducationControllerTest {

  @MockBean
  private EducationService educationService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private EducationDto educationDto;

  private List<EducationDto> educations;

  @BeforeEach
  public void before() {
    educationDto = EducationDto.builder()
        .id(1)
        .educationType("Course")
        .educationName("Hillel")
        .description("I learned a lot of knowledge")
        .startYear(2013)
        .endYear(2013)
        .build();

    educations = List.of(educationDto);
  }

  @Test
  @WithMockUser(username = "Hubersky", roles = {"USER", "ADMIN"})
  public void getAllTest() throws Exception {
    when(educationService.getAll()).thenReturn(educations);
    mockMvc.perform(get("/educations"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andDo(print());
    verify(educationService, times(1)).getAll();
  }

  @Test
  @WithMockUser(username = "Hubersky", roles = {"USER", "ADMIN"})
  public void getByIdTest() throws Exception {
    when(educationService.getById(anyLong())).thenReturn(educationDto);
    mockMvc.perform(get("/educations/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andDo(print());
    verify(educationService, times(1)).getById(anyLong());
  }

  @Test
  public void createTest() throws Exception {
    EducationDto educationDto1 = educationDto;
    when(educationService.create(anyLong(), any())).thenReturn(educationDto1);
    mockMvc.perform(post("/educations/{id}", 1)
            .with(SecurityMockMvcRequestPostProcessors.user("Hubersky").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(educationDto1)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1))
        .andDo(print());
    verify(educationService, times(1)).create(anyLong(), any());

  }

  @Test
  public void updateTest() throws Exception {
    when(educationService.update(anyLong(), any())).thenReturn(educationDto);
    mockMvc.perform(put("/educations/{id}", 1)
            .with(SecurityMockMvcRequestPostProcessors.user("Hubersky").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(educationDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1))
        .andDo(print());
    verify(educationService, times(1)).update(anyLong(), any());
  }

  @Test
  public void deleteTest() throws Exception {
    mockMvc.perform(delete("/educations/{id}", 1)
            .with(SecurityMockMvcRequestPostProcessors.user("Hubersky").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk());
  }
}
