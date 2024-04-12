package com.ratifire.devrate.controller;

import static com.ratifire.devrate.enums.LanguageProficiencyLevel.INTERMEDIATE_B1;
import static com.ratifire.devrate.enums.LanguageProficiencyLevel.NATIVE;
import static com.ratifire.devrate.enums.LanguageProficiencyName.ENGLISH;
import static com.ratifire.devrate.enums.LanguageProficiencyName.UKRAINE;
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
import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.service.user.UserService;
import java.util.Arrays;
import java.util.List;
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
 * Unit tests for the {@link UserController} class.
 */
@WebMvcTest(UserController.class)
@Import(SecurityConfiguration.class)
class UserControllerTest {

  private static final long USER_ID = 1L;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @MockBean
  private UserDetailsService userDetailsService;

  private UserDto userDto;

  private LanguageProficiencyDto languageProficiencyDto;

  @BeforeEach
  public void setUp() {
    userDto = UserDto.builder()
        .id(USER_ID)
        .firstName("firstName")
        .lastName("lastName")
        .position("position")
        .country("country")
        .region("region")
        .city("city")
        .subscribed(true)
        .description("description")
        .build();

    languageProficiencyDto = LanguageProficiencyDto.builder()
        .id(USER_ID)
        .name(ENGLISH)
        .level(INTERMEDIATE_B1)
        .build();
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void findByIdTest() throws Exception {
    when(userService.findById(USER_ID)).thenReturn(userDto);
    mockMvc.perform(get("/users/{id}", USER_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(userDto.getId()))
        .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(userDto.getLastName()))
        .andExpect(jsonPath("$.position").value(userDto.getPosition()))
        .andExpect(jsonPath("$.country").value(userDto.getCountry()))
        .andExpect(jsonPath("$.region").value(userDto.getRegion()))
        .andExpect(jsonPath("$.city").value(userDto.getCity()))
        .andExpect(jsonPath("$.subscribed").value(userDto.isSubscribed()))
        .andExpect(jsonPath("$.description").value(userDto.getDescription()));

    verify(userService, times(1)).findById(anyLong());
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void updateTest() throws Exception {
    String requestBody = objectMapper.writeValueAsString(userDto);
    when(userService.update(any(UserDto.class))).thenReturn(userDto);
    mockMvc.perform(put("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(userDto.getId()))
        .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(userDto.getLastName()))
        .andExpect(jsonPath("$.position").value(userDto.getPosition()))
        .andExpect(jsonPath("$.country").value(userDto.getCountry()))
        .andExpect(jsonPath("$.region").value(userDto.getRegion()))
        .andExpect(jsonPath("$.city").value(userDto.getCity()))
        .andExpect(jsonPath("$.subscribed").value(userDto.isSubscribed()))
        .andExpect(jsonPath("$.description").value(userDto.getDescription()));

    verify(userService, times(1)).update(any(UserDto.class));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void deleteTest() throws Exception {
    mockMvc.perform(delete("/users/{id}", USER_ID))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void createLanguageProficiencyTest() throws Exception {
    String requestBody = objectMapper.writeValueAsString(languageProficiencyDto);
    when(
        userService.createLanguageProficiency(anyLong(), any(LanguageProficiencyDto.class)))
        .thenReturn(languageProficiencyDto);
    mockMvc.perform(post("/users/{userId}/language-proficiencies", USER_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(languageProficiencyDto.getName().getCode()))
        .andExpect(jsonPath("$.level").value(languageProficiencyDto.getLevel()
            .toString()));
    verify(userService, times(1)).createLanguageProficiency(anyLong(),
        any(LanguageProficiencyDto.class));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void findAllLanguageProficienciesByUserIdTest() throws Exception {
    List<LanguageProficiencyDto> proficiencies = Arrays.asList(
        new LanguageProficiencyDto(1L, ENGLISH, NATIVE),
        new LanguageProficiencyDto(2L, UKRAINE, INTERMEDIATE_B1)
    );
    when(userService.findAllLanguageProficienciesByUserId(USER_ID)).thenReturn(proficiencies);
    mockMvc.perform(get("/users/{userId}/language-proficiencies", USER_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].name").value(ENGLISH.getCode()))
        .andExpect(jsonPath("$[0].level").value(NATIVE.toString()))
        .andExpect(jsonPath("$[1].name").value(UKRAINE.getCode()))
        .andExpect(jsonPath("$[1].level").value(INTERMEDIATE_B1.toString()));
    verify(userService, times(1))
        .findAllLanguageProficienciesByUserId(USER_ID);
  }

}