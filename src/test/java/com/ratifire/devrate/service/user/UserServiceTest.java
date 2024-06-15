package com.ratifire.devrate.service.user;

import static com.ratifire.devrate.enums.ContactType.GITHUB_LINK;
import static com.ratifire.devrate.enums.ContactType.TELEGRAM_LINK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.dto.BookmarkDto;
import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.entity.Achievement;
import com.ratifire.devrate.entity.Bookmark;
import com.ratifire.devrate.entity.Contact;
import com.ratifire.devrate.entity.Education;
import com.ratifire.devrate.entity.EmploymentRecord;
import com.ratifire.devrate.entity.LanguageProficiency;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.UserRepository;
import com.ratifire.devrate.service.specialization.SpecializationService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

  @Mock
  private SpecializationService specializationService;

  private final long userId = 1L;
  private User testUser;
  private EmploymentRecordDto employmentRecordDto;
  private EmploymentRecord employmentRecord;
  private List<LanguageProficiencyDto> languageProficiencyDtos;
  private final String userPicture = "123";

  private Achievement achievement;
  private AchievementDto achievementDto;
  private Education education;
  private Bookmark bookmark;
  private EducationDto educationDto;
  private BookmarkDto bookmarkDto;
  private List<ContactDto> contactDtos;
  private List<BookmarkDto> bookmarkDtos;
  private SpecializationDto specializationDto;
  private Specialization specialization;
  private List<SpecializationDto> specializationDtos;

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
        .bookmarks(new ArrayList<>())
        .specializations(new ArrayList<>())
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
        new LanguageProficiencyDto(1L, "ENGLISH", "EN", "ADVANCED|C1"),
        new LanguageProficiencyDto(2L, "FRENCH", "FR", "ADVANCED|C1")
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

    bookmark = Bookmark.builder()
        .id(1)
        .name("User1")
        .link("https:/user1")
        .build();

    bookmarkDto = BookmarkDto.builder()
        .id(1)
        .name("User1")
        .link("https:/user1")
        .build();

    contactDtos = Arrays.asList(
        new ContactDto(1L, TELEGRAM_LINK, "https://t.me/test"),
        new ContactDto(2L, GITHUB_LINK, "https://github.com/test")
    );

    bookmarkDtos = Arrays.asList(
        new BookmarkDto(1L, "User1", "https:/user1"),
        new BookmarkDto(2L, "User2", "https:/user2")
    );

    specializationDto = SpecializationDto.builder()
        .id(6661L)
        .name("Frontend Developer")
        .main(true)
        .build();

    specialization = Specialization.builder()
        .id(6661L)
        .main(true)
        .name("Frontend Developer")
        .completedInterviews(11)
        .conductedInterviews(4)
        .user(User.builder().build())
        .build();

    specializationDtos = Arrays.asList(
        specializationDto,
        SpecializationDto.builder().build()
    );
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
  void saveLanguageProficiencies_Successful_ReturnLanguageProficiencyDtos() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toEntity(any(LanguageProficiencyDto.class))).then(invocation -> {
      LanguageProficiencyDto proficiencyDto = invocation.getArgument(0);
      return new LanguageProficiency(proficiencyDto.getId(), proficiencyDto.getName(),
          proficiencyDto.getCode(), proficiencyDto.getLevel());
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
  void testAddUserPictureNewPicture() {
    User user = new User();
    user.setId(userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    userService.addUserPicture(userId, userPicture);

    assertEquals(userPicture, user.getPicture());
  }

  @Test
  void testDeleteUserPicture() {
    User user = new User();
    user.setId(userId);
    user.setPicture(userPicture);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    userService.deleteUserPicture(userId);

    assertNull(user.getPicture());
  }

  @Test
  void createAchievementTest() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    when(dataMapper.toEntity(achievementDto)).thenReturn(achievement);
    when(dataMapper.toDto(achievement)).thenReturn(achievementDto);

    AchievementDto result = userService.createAchievement(userId, achievementDto);
    assertEquals(achievementDto, result);
  }

  @Test
  void createEducationTest() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    when(dataMapper.toEntity(educationDto)).thenReturn(education);
    when(dataMapper.toDto(education)).thenReturn(educationDto);

    EducationDto result = userService.createEducation(userId, educationDto);
    assertEquals(educationDto, result);
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

  @Test
  void getBookmarks_Successful_ReturnBookmarkDtos() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toDto(anyList())).thenReturn(bookmarkDtos);

    List<BookmarkDto> result = userService.getBookmarksByUserId(userId);
    assertEquals(bookmarkDtos, result);
  }

  @Test
  void createBookmark_Successful() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toEntity(any(BookmarkDto.class))).thenReturn(bookmark);

    userService.createBookmark(userId, bookmarkDto);
    assertTrue(testUser.getBookmarks().contains(bookmark));
  }

  @Test
  void createBookmark_UserNotFound_ThrowsUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.createBookmark(userId, bookmarkDto));
  }

  @Test
  public void testGetSpecializationsByUserId() {
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    when(dataMapper.toDto(anyList())).thenReturn(specializationDtos);

    List<SpecializationDto> result = userService.getSpecializationsByUserId(userId);
    assertEquals(specializationDtos, result);
  }

  @Test
  public void testCreateSpecialization() {
    when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(testUser));
    when(dataMapper.toEntity(specializationDto)).thenReturn(specialization);

    userService.createSpecialization(specializationDto, userId);
    assertTrue(testUser.getSpecializations().contains(specialization));
  }
}
