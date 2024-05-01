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
import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.service.AchievementService;
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
 * Unit tests for the AchievementController class.
 */
@WebMvcTest(AchievementController.class)
public class AchievementControllerTest {

  @MockBean
  private AchievementService achievementService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private AchievementDto achievementDto;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void before() {
    achievementDto = AchievementDto.builder()
        .id(1).link("https://certificate.ithillel.ua/view/86277355")
        .summary("summary")
        .description("description")
        .build();
  }

  @Test
  @WithMockUser(username = "Hubersky", roles = {"USER", "ADMIN"})
  public void getByIdTest() throws Exception {
    when(achievementService.getById(anyLong())).thenReturn(achievementDto);

    String responseAsString = mockMvc.perform(get("/achievements/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse()
        .getContentAsString();

    AchievementDto result = objectMapper.readValue(responseAsString, AchievementDto.class);

    assertEquals(achievementDto, result);
  }

  @Test
  public void updateTest() throws Exception {
    when(achievementService.update(anyLong(), any())).thenReturn(achievementDto);

    String responseAsString = mockMvc.perform(put("/achievements/{id}", 1)
            .with(SecurityMockMvcRequestPostProcessors.user("Hubersky").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(achievementDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    AchievementDto result = objectMapper.readValue(responseAsString, AchievementDto.class);

    assertEquals(achievementDto, result);
  }

  @Test
  public void deleteTest() throws Exception {
    mockMvc.perform(delete("/achievements/{id}", 1)
            .with(SecurityMockMvcRequestPostProcessors.user("Hubersky").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk());
  }
}
