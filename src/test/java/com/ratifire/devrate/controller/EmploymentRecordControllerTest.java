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
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.service.employmentrecord.EmploymentRecordService;
import java.time.LocalDate;
import java.util.Arrays;
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
 * Unit tests for the EmploymentRecordController class.
 */
@WebMvcTest(EmploymentRecordController.class)
public class EmploymentRecordControllerTest {

  @MockBean
  private EmploymentRecordService employmentRecordService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private EmploymentRecordDto employmentRecordDto;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void before() {
    employmentRecordDto = EmploymentRecordDto.builder()
        .id(1L)
        .startDate(LocalDate.ofEpochDay(2023 - 01 - 01))
        .endDate(LocalDate.ofEpochDay(2022 - 01 - 01))
        .position("Java Developer")
        .companyName("New Company")
        .description("Worked on various projects")
        .responsibilities(Arrays.asList("1", "2", "3")) // Создание списка из строк
        .build();
  }

  @Test
  @WithMockUser(username = "Maksim Matveychuk", roles = {"USER", "ADMIN"})
  public void getByIdTest() throws Exception {
    when(employmentRecordService.findByUserId(anyLong())).thenReturn(
        Collections.singletonList(employmentRecordDto));
    mockMvc.perform(get("/user/{userId}/employment-record", 1))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1))
        .andDo(print());
    verify(employmentRecordService, times(1)).findByUserId(anyLong());
  }

  @Test
  public void createTest() throws Exception {
    EmploymentRecordDto employmentRecordDto1 = employmentRecordDto;
    when(employmentRecordService.create(employmentRecordDto, 8883L)).thenReturn(
        employmentRecordDto1);
    mockMvc.perform(post("/user/8883/employment-record", 1)
            .with(SecurityMockMvcRequestPostProcessors.user("Maksim Matveychuk").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employmentRecordDto1)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1))
        .andDo(print());
    verify(employmentRecordService, times(1))
        .create(employmentRecordDto, 8883L);

  }

  @Test
  public void updateTest() throws Exception {
    when(employmentRecordService.update(employmentRecordDto)).thenReturn(employmentRecordDto);
    mockMvc.perform(put("/user/{userId}/employment-record", 1)
            .with(SecurityMockMvcRequestPostProcessors.user("Maksim Matveychuk").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employmentRecordDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpect(jsonPath("$.id").value(1))
        .andDo(print());
    verify(employmentRecordService, times(1)).update(employmentRecordDto);
  }

  @Test
  public void deleteTest() throws Exception {
    mockMvc.perform(delete("/user/{userId}/employment-record/{id}", 1, 1L)
            .with(SecurityMockMvcRequestPostProcessors.user("Maksim Matveychuk").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk());
  }
}
