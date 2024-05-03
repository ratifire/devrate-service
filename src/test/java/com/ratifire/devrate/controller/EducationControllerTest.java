package com.ratifire.devrate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.service.EducationService;
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
 * Unit tests for the EducationController class.
 */
@WebMvcTest(EducationController.class)
public class EducationControllerTest {

  @MockBean
  private EducationService educationService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private EducationDto educationDto;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void before() {
    educationDto = EducationDto.builder()
        .id(1)
        .type("Course")
        .name("Hillel")
        .description("I learned a lot of knowledge")
        .startYear(2013)
        .endYear(2013)
        .build();
  }

  @Test
  @WithMockUser(username = "Hubersky", roles = {"USER", "ADMIN"})
  public void getByIdTest() throws Exception {
    when(educationService.getById(anyLong())).thenReturn(educationDto);

    String responseAsString = mockMvc.perform(get("/educations/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse()
        .getContentAsString();

    EducationDto resultEducationDto = objectMapper.readValue(responseAsString, EducationDto.class);

    assertEquals(educationDto, resultEducationDto);
  }

  @Test
  public void updateTest() throws Exception {
    when(educationService.update(anyLong(), any())).thenReturn(educationDto);

    String responseAsString = mockMvc.perform(put("/educations/{id}", 1)
            .with(SecurityMockMvcRequestPostProcessors.user("Hubersky").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(educationDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    EducationDto resultEducationDto = objectMapper.readValue(responseAsString, EducationDto.class);

    assertEquals(educationDto, resultEducationDto);
  }

  @Test
  public void deleteTest() throws Exception {
    mockMvc.perform(delete("/educations/{id}", 1)
            .with(SecurityMockMvcRequestPostProcessors.user("Hubersky").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk());
  }
}
