package com.ratifire.devrate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.configuration.SecurityConfiguration;
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.service.employmentrecord.EmploymentRecordService;
import com.ratifire.devrate.service.user.UserService;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
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
  @MockBean
  private EmploymentRecordService employmentRecordService;
  @MockBean
  private EmploymentRecordDto employmentRecordDto;


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

    employmentRecordDto = EmploymentRecordDto.builder()
        .id(1L)
        .startDate(LocalDate.ofEpochDay(2023 - 1 - 1))
        .endDate(LocalDate.ofEpochDay(2022 - 1 - 1))
        .position("Java Developer")
        .companyName("New Company")
        .description("Worked on various projects")
        .responsibilities(Arrays.asList("1", "2", "3")) // Создание списка из строк
        .build();
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void findByIdTest() throws Exception {
    when(userService.findById(USER_ID)).thenReturn(userDto);

    String responseAsString = mockMvc.perform(get("/users/{id}", USER_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse()
        .getContentAsString();

    UserDto resultUserDto = objectMapper.readValue(responseAsString, UserDto.class);

    assertEquals(userDto, resultUserDto);
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void updateTest() throws Exception {
    String requestBody = objectMapper.writeValueAsString(userDto);
    when(userService.update(any(UserDto.class))).thenReturn(userDto);

    String responseAsString = mockMvc.perform(put("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    UserDto resultUserDto = objectMapper.readValue(responseAsString, UserDto.class);

    assertEquals(userDto, resultUserDto);
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void deleteTest() throws Exception {
    mockMvc.perform(delete("/users/{id}", USER_ID))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "Maksim Matveychuk", roles = {"USER", "ADMIN"})
  public void getByIdTest() throws Exception {
    List<EmploymentRecordDto> expectedDtoList = Collections.singletonList(employmentRecordDto);
    when(userService.getEmploymentRecordsByUserId(anyLong())).thenReturn(
        Collections.singletonList(employmentRecordDto));
    String responseAsString = mockMvc
        .perform(get("/users/{userId}/employment-records", USER_ID))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse()
        .getContentAsString();

    List<EmploymentRecordDto> actualDtoList = objectMapper.readValue(responseAsString,
        new TypeReference<List<EmploymentRecordDto>>() {
        });
    assertEquals(expectedDtoList, actualDtoList);

  }

  @Test
  public void createEmploymentRecordTest() throws Exception {
    EmploymentRecordDto employmentRecordDto1 = employmentRecordDto;
    when(userService.createEmploymentRecord(employmentRecordDto, USER_ID)).thenReturn(
        employmentRecordDto1);
    String responseAsString = mockMvc
        .perform(post("/users/{userId}/employment-records", USER_ID)
            .with(SecurityMockMvcRequestPostProcessors
                .user("Maksim Matveychuk").roles("USER"))
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employmentRecordDto1)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    EmploymentRecordDto resultEmploymentRecordDto = objectMapper.readValue(responseAsString,
        EmploymentRecordDto.class);

    assertEquals(employmentRecordDto, resultEmploymentRecordDto);
  }
}