package com.ratifire.devrate.controller;


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
import com.ratifire.devrate.dto.WorkExperienceDto;
import com.ratifire.devrate.service.workexperience.WorkExperienceService;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for the WorkExperienceController class.
 */
@WebMvcTest(WorkExperienceController.class)
public class WorkExperienceControllerTest {

  @MockBean
  private WorkExperienceService workExperienceService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private WorkExperienceDto workExperienceDto;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void before() {
    workExperienceDto = WorkExperienceDto.builder()
        .id(1L)
        .startDate(LocalDate.ofEpochDay(2023 - 01 - 01))
        .endDate(LocalDate.ofEpochDay(2022 - 01 - 01))
        .position("Java Developer")
        .companyName("New Company")
        .description("Worked on various projects")
        .userId(8882)
        .responsibilityId("1, 2, 3")
        .build();
  }

  @Test
  @WithMockUser(username = "Maksim Matveychuk", roles = {"USER", "ADMIN"})
  public void getByIdTest() throws Exception {
    when(workExperienceService.findByUserId(anyLong())).thenReturn(
        Collections.singletonList(workExperienceDto));
    mockMvc.perform(get("/work-experience/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1))
        .andDo(print());
    verify(workExperienceService, times(1)).findByUserId(anyLong());
  }

  @Test
  public void createTest() throws Exception {
    WorkExperienceDto workExperienceDto1 = workExperienceDto;
    when(workExperienceService.create(workExperienceDto)).thenReturn(workExperienceDto1);
    mockMvc.perform(post("/work-experience", 1)
            .with(SecurityMockMvcRequestPostProcessors.user("Maksim Matveychuk").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(workExperienceDto1)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1))
        .andDo(print());
    verify(workExperienceService, times(1)).create(workExperienceDto);

  }

  @Test
  public void updateTest() throws Exception {
    when(workExperienceService.update(workExperienceDto)).thenReturn(workExperienceDto);
    mockMvc.perform(put("/work-experience", 1)
            .with(SecurityMockMvcRequestPostProcessors.user("Maksim Matveychuk").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(workExperienceDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1))
        .andDo(print());
    verify(workExperienceService, times(1)).update(workExperienceDto);
  }

  @Test
  public void deleteTest() throws Exception {
    mockMvc.perform(delete("/work-experience/{id}", 1)
            .with(SecurityMockMvcRequestPostProcessors.user("Maksim Matveychuk").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk());
  }
}
