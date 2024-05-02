package com.ratifire.devrate.controller;


import static com.ratifire.devrate.enums.ContactType.GITHUB_LINK;
import static com.ratifire.devrate.enums.ContactType.TELEGRAM_LINK;
import static com.ratifire.devrate.enums.LanguageProficiencyLevel.INTERMEDIATE_B1;
import static com.ratifire.devrate.enums.LanguageProficiencyLevel.NATIVE;
import static com.ratifire.devrate.enums.LanguageProficiencyName.ENGLISH;
import static com.ratifire.devrate.enums.LanguageProficiencyName.UKRAINE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.configuration.SecurityConfiguration;
import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.dto.PictureDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.service.AchievementService;
import com.ratifire.devrate.service.employmentrecord.EmploymentRecordService;
import com.ratifire.devrate.service.user.UserService;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
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
  private EmploymentRecordDto employmentRecordDto;

  private final long userId = 1L;
  private final byte[] userPicture = new byte[] {1, 2, 3};
  private final String base64UserPicture = Base64.getEncoder().encodeToString(userPicture);
  PictureDto userPictureDto = new PictureDto(userPicture);

  private List<LanguageProficiencyDto> languageProficiencyDtos;
  private List<ContactDto> contactDtos;

  @MockBean
  private AchievementService achievementService;
  private AchievementDto achievementDto;
  private List<AchievementDto> achievementDtoList;


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
    
    employmentRecordDto =
        EmploymentRecordDto.builder()
            .id(1L)
            .startDate(LocalDate.ofEpochDay(2023 - 1 - 1))
            .endDate(LocalDate.ofEpochDay(2022 - 1 - 1))
            .position("Java Developer")
            .companyName("New Company")
            .description("Worked on various projects")
            .responsibilities(Arrays.asList("1", "2", "3")) // Создание списка из строк
            .build();
    
    employmentRecordDto = EmploymentRecordDto.builder()
        .id(1L)
        .startDate(LocalDate.ofEpochDay(2023 - 1 - 1))
        .endDate(LocalDate.ofEpochDay(2022 - 1 - 1))
        .position("Java Developer")
        .companyName("New Company")
        .description("Worked on various projects")
        .responsibilities(Arrays.asList("1", "2", "3"))
        .build();

    achievementDto = AchievementDto.builder()
        .id(1).link("https://certificate.ithillel.ua/view/86277355")
        .summary("summary")
        .description("description")
        .build();
    achievementDtoList = List.of(achievementDto);

    languageProficiencyDtos = Arrays.asList(
        new LanguageProficiencyDto(1L, UKRAINE, NATIVE),
        new LanguageProficiencyDto(2L, ENGLISH, INTERMEDIATE_B1)
    );

    contactDtos = Arrays.asList(
        new ContactDto(1L, TELEGRAM_LINK, "https://t.me/test"),
        new ContactDto(2L, GITHUB_LINK, "https://github.com/test")
    );
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
  void getByIdTest() throws Exception {
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
  void createEmploymentRecordTest() throws Exception {
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

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  public void getUserPictureWhenExists() throws Exception {

    when(userService.getUserPicture(userId)).thenReturn(userPictureDto);

    mockMvc
        .perform(get("/users/{userId}/pictures", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.picture").value(Base64.getEncoder().encodeToString(userPicture)));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  public void addUserPictureTest() throws Exception {

    when(userService.addUserPicture(anyLong(), any(byte[].class))).thenReturn(userPictureDto);

    mockMvc
        .perform(
            post("/users/{userId}/pictures", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new PictureDto(userPicture))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.picture").value(Base64.getEncoder().encodeToString(userPicture)));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  public void removeUserPictureTest() throws Exception {
    long userId = 1L;

    doNothing().when(userService).deleteUserPicture(userId);

    mockMvc.perform(delete("/users/{userId}/pictures", userId)).andExpect(status().isOk());

  }

  @Test
  @WithMockUser(username = "Hubersky", roles = {"USER", "ADMIN"})
  public void getAchievementsByUserIdTest() throws Exception {
    when(userService.getAchievementsByUserId(anyLong())).thenReturn(achievementDtoList);
    mockMvc.perform(get("/users/{userId}/achievements", USER_ID))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(achievementDtoList)));
  }

  @Test
  @WithMockUser(username = "Hubersky", roles = {"USER", "ADMIN"})
  public void createAchievementTest() throws Exception {
    when(userService.createAchievement(anyLong(), any()))
        .thenReturn(achievementDto);
    mockMvc.perform(post("/users/{userId}/achievements", USER_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(achievementDto)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(achievementDto)));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void saveContactsTest() throws Exception {
    String content = objectMapper.writeValueAsString(contactDtos);
    when(userService.saveContacts(anyLong(), anyList())).thenReturn(contactDtos);
    mockMvc.perform(post("/users/{userId}/contacts", USER_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(status().isOk())
        .andExpect(content().json(content));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void findAllContactsByUserIdTest() throws Exception {
    String expectedContent = objectMapper.writeValueAsString(contactDtos);
    when(userService.findAllContactsByUserId(anyLong())).thenReturn(contactDtos);
    mockMvc.perform(get("/users/{userId}/contacts", USER_ID))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedContent));
  }
}
