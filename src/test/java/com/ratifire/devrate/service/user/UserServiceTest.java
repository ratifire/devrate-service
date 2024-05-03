package com.ratifire.devrate.service.user;

import static com.ratifire.devrate.enums.ContactType.GITHUB_LINK;
import static com.ratifire.devrate.enums.ContactType.TELEGRAM_LINK;
import static com.ratifire.devrate.enums.LanguageProficiencyLevel.INTERMEDIATE_B1;
import static com.ratifire.devrate.enums.LanguageProficiencyLevel.NATIVE;
import static com.ratifire.devrate.enums.LanguageProficiencyName.ENGLISH;
import static com.ratifire.devrate.enums.LanguageProficiencyName.UKRAINE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.Achievement;
import com.ratifire.devrate.entity.Contact;
import com.ratifire.devrate.entity.Education;
import com.ratifire.devrate.entity.EmploymentRecord;
import com.ratifire.devrate.entity.LanguageProficiency;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the UserService class.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private DataMapper dataMapper;

  @Mock
  private UserRepository userRepository;

  private final long userId = 1L;
  private User testUser;
  private UserDto testUserDto;
  private EmploymentRecordDto employmentRecordDto;
  private EmploymentRecord employmentRecord;
  private List<LanguageProficiencyDto> languageProficiencyDtos;
  private final  byte[] picture = new byte[] {4, 5, 6};
  @Captor
  private ArgumentCaptor<User> userCaptor;

  private Achievement achievement;
  private AchievementDto achievementDto;
  private List<AchievementDto> achievementDtoList;
  private Education education;
  private EducationDto educationDto;
  private List<EducationDto> educationDtoList;
  private List<ContactDto> contactDtos;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void init() {
    testUser = User.builder()
        .id(userId)
        .employmentRecords(new ArrayList<>())
        .languageProficiencies(new ArrayList<>())
        .achievements(new ArrayList<>())
        .educations(new ArrayList<>())
        .contacts(new ArrayList<>())
        .build();

    testUserDto = UserDto.builder()
        .id(userId)
        .build();

    employmentRecordDto = EmploymentRecordDto.builder()
        .id(1L)
        .startDate(LocalDate.ofEpochDay(2023 - 01 - 01))
        .endDate(LocalDate.ofEpochDay(2022 - 01 - 01))
        .position("Java Developer")
        .companyName("New Company")
        .description("Worked on various projects")
        .responsibilities(Arrays.asList("1", "2", "3"))
        .build();

    employmentRecord = EmploymentRecord.builder()
        .id(1L)
        .startDate(LocalDate.of(2023, 1, 1))
        .endDate(LocalDate.of(2023, 11, 22))
        .position("Java Developer")
        .companyName("Example Company 4")
        .description("Worked on various projects")
        .responsibilities(Arrays.asList("Собирал одуванчики", "Hello World", "3"))
        .build();

    languageProficiencyDtos = Arrays.asList(
        new LanguageProficiencyDto(1L, ENGLISH, NATIVE),
        new LanguageProficiencyDto(2L, UKRAINE, INTERMEDIATE_B1)
    );

    achievement = Achievement.builder()
        .id(1).link("https://certificate.ithillel.ua/view/86277355")
        .summary("summary")
        .description("description")
        .build();

    achievementDto = AchievementDto.builder()
        .id(1).link("https://certificate.ithillel.ua/view/86277355")
        .summary("summary")
        .description("description")
        .build();
    achievementDtoList = List.of(achievementDto);

    education = Education.builder()
        .id(1)
        .type("Course")
        .name("Hillel")
        .description("I learned a lot of knowledge")
        .startYear(2013)
        .endYear(2013)
        .build();

    educationDto = EducationDto.builder()
        .id(1)
        .type("Course")
        .name("Hillel")
        .description("I learned a lot of knowledge")
        .startYear(2013)
        .endYear(2013)
        .build();
    educationDtoList = List.of(educationDto);

    contactDtos = Arrays.asList(
        new ContactDto(1L, TELEGRAM_LINK, "https://t.me/test"),
        new ContactDto(2L, GITHUB_LINK, "https://github.com/test")
    );
  }

  @Test
  void findUserById_UserExists_ReturnsUserDto() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toDto(any(User.class))).thenReturn(testUserDto);

    UserDto foundUser = userService.findById(userId);

    assertEquals(foundUser, testUserDto);
  }

  @Test
  void findUserById_UserNotFound_ThrowsUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
  }

  @Test
  void whenCreate_Successful_ReturnCreatedUser() {
    when(dataMapper.toEntity(any(UserDto.class))).thenReturn(testUser);
    when(userRepository.save(any())).thenReturn(testUser);

    User createdUser = userService.create(testUserDto);

    assertEquals(testUserDto.getId(), createdUser.getId());
    verify(userRepository).save(testUser);
    verify(dataMapper).toEntity(testUserDto);
  }

  @Test
  void whenUpdate_UserNotFound_ThrowUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.update(testUserDto));
  }

  @Test
  void whenUpdate_Successful_ReturnCreatedUserDto() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.updateEntity(any(UserDto.class), any(User.class))).thenReturn(testUser);
    when(userRepository.save(any())).thenReturn(testUser);
    when(dataMapper.toDto(any(User.class))).thenReturn(testUserDto);

    UserDto updatedUserDto = userService.update(testUserDto);

    assertEquals(testUserDto.getId(), updatedUserDto.getId());
    verify(userRepository).findById(userId);
    verify(userRepository).save(testUser);
    verify(dataMapper).updateEntity(testUserDto, testUser);
    verify(dataMapper).toDto(testUser);
  }

  @Test
  void whenUpdate_Successful_ReturnCreatedUser() {
    when(userRepository.save(any())).thenReturn(testUser);

    User updatedUser = userService.updateUser(testUser);

    assertEquals(userId, updatedUser.getId());
    verify(userRepository, times(1)).save(testUser);
  }

  @Test
  void whenDelete_UserNotFound_ThrowUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
  }

  @Test
  void delete_UserExists_DeletesUser() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));

    userService.delete(userId);

    verify(userRepository, times(1)).delete(testUser);
  }

  @Test
  void createEmploymentRecord_Successful_ReturnCreatedEmploymentRecordDto() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toEntity(any(EmploymentRecordDto.class))).thenReturn(
        employmentRecord);
    when(dataMapper.toDto(any(EmploymentRecord.class))).thenReturn(
        employmentRecordDto);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    EmploymentRecordDto result = userService.createEmploymentRecord(employmentRecordDto, userId);

    assertEquals(employmentRecordDto, result);
  }

  @Test
  void getEmploymentRecordsByUserId_UserExists_ReturnsListOfEmploymentRecordDto() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.getEmploymentRecordsByUserId(userId));
  }

  @Test
  void findAllLanguageProficienciesByUserId_UserExists_ReturnsListOfLanguageProficiencyDto() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));

    List<LanguageProficiencyDto> result = userService.findAllLanguageProficienciesByUserId(userId);

    assertEquals(0, result.size());
  }

  @Test
  void findAllLanguageProficienciesByUserId_UserNotFound_ThrowsUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.findAllLanguageProficienciesByUserId(userId));
  }

  @Test
  void saveLanguageProficiencies_Successful_ReturnLanguageProficiencyDtos() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toEntity(any(LanguageProficiencyDto.class))).then(invocation -> {
      LanguageProficiencyDto proficiencyDto = invocation.getArgument(0);
      return new LanguageProficiency(proficiencyDto.getId(), proficiencyDto.getName(),
          proficiencyDto.getLevel());
    });
    when(dataMapper.toDto(anyList())).thenReturn(languageProficiencyDtos);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    List<LanguageProficiencyDto> result = userService.saveLanguageProficiencies(userId,
        languageProficiencyDtos);

    assertEquals(languageProficiencyDtos, result);
  }

  @Test
  void saveLanguageProficiencies_UserNotFound_ThrowsUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.saveLanguageProficiencies(userId, languageProficiencyDtos));
  }

  @Test
  public void testGetUserPictureExists() {
    when(userRepository.findPictureByUserId(userId)).thenReturn(picture);

    byte[] actualPicture = userService.getUserPicture(userId);

    assertNotNull(actualPicture);
    assertArrayEquals(picture, actualPicture);
  }

  @Test
  public void testAddUserPictureNewPicture() {
    User user = new User();
    user.setId(userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    userService.addUserPicture(userId, picture);

    assertArrayEquals(picture, user.getPicture());

  }

  @Test
  public void testDeleteUserPicture() {
    User user = new User();
    user.setId(userId);
    user.setPicture(picture);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    userService.deleteUserPicture(userId);

    assertNull(user.getPicture());
  }

  @Test
  public void getAchievementsByUserIdTest() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toDto(any())).thenReturn(achievementDtoList);

    List<AchievementDto> result = userService.getAchievementsByUserId(userId);
    assertEquals(achievementDtoList, result);
  }

  @Test
  public void createAchievementTest() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    when(dataMapper.toEntity(achievementDto)).thenReturn(achievement);
    when(dataMapper.toDto(achievement)).thenReturn(achievementDto);

    AchievementDto result = userService.createAchievement(userId, achievementDto);
    assertEquals(achievementDto, result);
  }

  @Test
  public void getEducationsByUserIdTest() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toDto(any())).thenReturn(educationDtoList);

    List<EducationDto> result = userService.getEducationsByUserId(userId);
    assertEquals(educationDtoList, result);
  }

  @Test
  public void createEducationTest() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    when(dataMapper.toEntity(educationDto)).thenReturn(education);
    when(dataMapper.toDto(education)).thenReturn(educationDto);

    EducationDto result = userService.createEducation(userId, educationDto);
    assertEquals(educationDto, result);
  }

  @Test
  void findAllContactsByUserId_UserExists_ReturnsListOfContactDto() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));

    List<ContactDto> result = userService.findAllContactsByUserId(userId);

    assertEquals(0, result.size());
  }

  @Test
  void findAllContactsByUserId_UserNotFound_ThrowsUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.findAllContactsByUserId(userId));
  }

  @Test
  void saveContacts_Successful_ReturnContactDtos() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toEntity(any(ContactDto.class))).then(invocation -> {
      ContactDto contactDto = invocation.getArgument(0);
      return new Contact(contactDto.getId(), contactDto.getType(), contactDto.getValue());
    });
    when(dataMapper.toDto(anyList())).thenReturn(contactDtos);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    List<ContactDto> result = userService.saveContacts(userId, contactDtos);

    assertEquals(contactDtos, result);
  }

  @Test
  void saveContacts_UserNotFound_ThrowsUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.saveContacts(userId, contactDtos));
  }
}

