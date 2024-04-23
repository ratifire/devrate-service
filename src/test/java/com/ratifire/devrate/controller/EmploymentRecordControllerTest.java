package com.ratifire.devrate.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.service.employmentrecord.EmploymentRecordService;
import java.time.LocalDate;
import java.util.Arrays;
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
        .startDate(LocalDate.ofEpochDay(2023 - 1 - 1))
        .endDate(LocalDate.ofEpochDay(2022 - 1 - 1))
        .position("Java Developer")
        .companyName("New Company")
        .description("Worked on various projects")
        .responsibilities(Arrays.asList("1", "2", "3"))
        .build();
  }

  @Test
  @WithMockUser(username = "Maksim Matveychuk", roles = {"USER", "ADMIN"})
  public void getByIdTest() throws Exception {
    when(employmentRecordService.findById(anyLong())).thenReturn(
        employmentRecordDto);
    String responseAsString = mockMvc
        .perform(get("/employment-records/{id}", 1))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse()
        .getContentAsString();

    EmploymentRecordDto resultEmploymentRecordDto = objectMapper
        .readValue(responseAsString, EmploymentRecordDto.class);

    assertEquals(employmentRecordDto, resultEmploymentRecordDto);
  }


  @Test
  public void deleteTest() throws Exception {
    mockMvc.perform(delete("/employment-records/{id}", 1, 1L)
            .with(SecurityMockMvcRequestPostProcessors
                .user("Maksim Matveychuk").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk());
  }
}
