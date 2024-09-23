package com.ratifire.devrate.service.user;

import static com.ratifire.devrate.enums.ContactType.GITHUB_LINK;
import static com.ratifire.devrate.enums.ContactType.TELEGRAM_LINK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.dto.BookmarkDto;
import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.EventDto;
import com.ratifire.devrate.dto.InterviewRequestDto;
import com.ratifire.devrate.dto.InterviewStatsConductedPassedByDateDto;
import com.ratifire.devrate.dto.InterviewSummaryDto;
import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.dto.UserMainMasterySkillDto;
import com.ratifire.devrate.entity.Achievement;
import com.ratifire.devrate.entity.Bookmark;
import com.ratifire.devrate.entity.Contact;
import com.ratifire.devrate.entity.Education;
import com.ratifire.devrate.entity.EmploymentRecord;
import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.InterviewSummary;
import com.ratifire.devrate.entity.LanguageProficiency;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.exception.InterviewSummaryNotFoundException;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.InterviewSummaryRepository;
import com.ratifire.devrate.repository.UserRepository;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.email.EmailService;
import com.ratifire.devrate.service.interview.InterviewMatchingService;
import com.ratifire.devrate.service.interview.InterviewRequestService;
import com.ratifire.devrate.service.interview.InterviewService;
import com.ratifire.devrate.service.specialization.SpecializationService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
  private UserSecurityService userSecurityService;

  @Mock
  private InterviewSummaryRepository interviewSummaryRepository;

  @Mock
  private InterviewMatchingService interviewMatchingService;

  @Mock
  private InterviewRequestService interviewRequestService;

  @Mock
  private InterviewService interviewService;

  @Mock
  private EmailService emailService;

  @Mock
  private NotificationService notificationService;

  @Mock
  private SpecializationService specializationService;

  private final long userId = 1L;
  private final long interviewSummaryId = 1L;
  private LocalDate fromDate;
  private LocalDate toDate;
  private User testUser;
  private InterviewSummary testInterviewSummary;
  private EmploymentRecordDto employmentRecordDto;
  private EmploymentRecord employmentRecord;
  private List<LanguageProficiencyDto> languageProficiencyDtos;
  private final String userPicture = "123";
  private final String userEmail = "user@gmail.com";

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
  private List<InterviewSummaryDto> interviewSummaryDtos;
  private List<UserMainMasterySkillDto> userMainMasterySkillDtos;
  private InterviewSummary candidateSummary;
  private InterviewSummary interviewerSummary;
  private InterviewRequest interviewRequest;
  private InterviewRequestDto interviewRequestDto;
  private Interview interview;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void init() {
    userService.setUserRepository(userRepository);

    testUser = User.builder()
        .id(userId)
        .employmentRecords(new ArrayList<>())
        .languageProficiencies(new ArrayList<>())
        .achievements(new ArrayList<>())
        .educations(new ArrayList<>())
        .contacts(new ArrayList<>())
        .bookmarks(new ArrayList<>())
        .specializations(new ArrayList<>())
        .interviewSummaries(new ArrayList<>())
        .build();

    testInterviewSummary = InterviewSummary.builder()
        .id(interviewSummaryId)
        .build();
    testUser.getInterviewSummaries().add(testInterviewSummary);

    employmentRecordDto = EmploymentRecordDto.builder()
        .id(1L)
        .startYear(1997)
        .endYear(1998)
        .position("Java Developer")
        .companyName("New Company")
        .description("Worked on various projects")
        .responsibilities(Arrays.asList("1", "2", "3"))
        .build();

    employmentRecord = EmploymentRecord.builder()
        .id(1L)
        .startYear(1998)
        .endYear(1999)
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

    interviewSummaryDtos = Arrays.asList(
        new InterviewSummaryDto(1L, LocalDate.of(
            2023, 6, 14), 60L, 1L, 2L),
        new InterviewSummaryDto(2L, LocalDate.of(
            2023, 6, 15), 45L, 1L, 3L)
    );

    userMainMasterySkillDtos = List.of(UserMainMasterySkillDto
        .builder()
        .specialization(specializationDto)
        .mainMastery(MasteryDto.builder()
            .id(1L)
            .level("JUNIOR")
            .hardSkillMark(BigDecimal.valueOf(5))
            .softSkillMark(BigDecimal.valueOf(5))
            .build())
        .mainMasterySkills(Arrays.asList(SkillDto.builder()
                .id(10L)
                .name("SQL")
                .hidden(true)
                .averageMark(BigDecimal.valueOf(5))
                .grows(false)
                .build(),
            SkillDto.builder()
                .id(11L)
                .name("Spring")
                .hidden(false)
                .averageMark(BigDecimal.valueOf(7))
                .grows(true)
                .build()))
        .build());

    fromDate = LocalDate.of(2023, 6, 1);
    toDate = LocalDate.of(2023, 6, 30);

    candidateSummary = InterviewSummary.builder()
        .id(1L)
        .date(LocalDate.of(2023, 6, 14))
        .candidateId(userId)
        .interviewerId(2L)
        .build();

    interviewerSummary = InterviewSummary.builder()
        .id(2L)
        .date(LocalDate.of(2023, 6, 15))
        .candidateId(3L)
        .interviewerId(userId)
        .build();

    interviewRequest = InterviewRequest.builder()
        .user(testUser)
        .build();
    interviewRequestDto = InterviewRequestDto.builder().build();
    interview = Interview.builder()
        .id(1L)
        .interviewerRequest(interviewRequest)
        .candidateRequest(interviewRequest)
        .startTime(ZonedDateTime.now())
        .build();
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
  void testGetSpecializationsByUserId() {
    when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
    when(dataMapper.toDto(anyList())).thenReturn(specializationDtos);

    List<SpecializationDto> result = userService.getSpecializationsByUserId(userId);
    assertEquals(specializationDtos, result);
  }

  @Test
  void testCreateSpecialization() {
    when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(testUser));
    when(dataMapper.toEntity(specializationDto)).thenReturn(specialization);

    userService.createSpecialization(specializationDto, userId);
    assertTrue(testUser.getSpecializations().contains(specialization));
  }

  @Test
  void getInterviewSummaries_Successful_ReturnInterviewSummaryDtos() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toDto(anyList())).thenReturn(interviewSummaryDtos);

    List<InterviewSummaryDto> result = userService.getInterviewSummariesByUserId(userId);

    assertEquals(interviewSummaryDtos, result);
  }

  @Test
  void deleteInterviewSummary_Successful() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(interviewSummaryRepository.findById(any())).thenReturn(Optional.of(testInterviewSummary));

    userService.deleteInterviewSummary(userId, interviewSummaryId);

    assertFalse(testUser.getInterviewSummaries().contains(testInterviewSummary));
  }

  @Test
  void deleteInterviewSummary_InterviewSummaryNotFound_ThrowsInterviewSummaryNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(interviewSummaryRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(InterviewSummaryNotFoundException.class,
        () -> userService.deleteInterviewSummary(userId, interviewSummaryId));
  }

  @Test
  void getPrivateMainMasterySkillsByUserId_Successful() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toDto(any())).thenReturn(userMainMasterySkillDtos);

    List<UserMainMasterySkillDto> result = userService.getPrivateMainMasterySkillsByUserId(userId);
    assertEquals(userMainMasterySkillDtos, result);
  }

  @Test
  void getPublicMainMasterySkillsByUserId_Successful() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toDto(any())).thenReturn(userMainMasterySkillDtos);

    List<UserMainMasterySkillDto> result = userService.getPublicMainMasterySkillsByUserId(userId);
    assertEquals(userMainMasterySkillDtos, result);
  }

  @Test
  void testGetInterviewStatConductedPassedByDate() {
    when(interviewSummaryRepository.findByCandidateOrInterviewerAndDateBetween(anyLong(),
        any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Arrays.asList(candidateSummary, interviewerSummary));

    List<InterviewStatsConductedPassedByDateDto> result = userService
        .getInterviewStatConductedPassedByDate(userId, fromDate, toDate);

    assertEquals(2, result.size());

    InterviewStatsConductedPassedByDateDto passedDto = result.stream()
        .filter(dto -> dto.getDate().equals(candidateSummary.getDate()))
        .findFirst().orElse(null);
    InterviewStatsConductedPassedByDateDto conductedDto = result.stream()
        .filter(dto -> dto.getDate().equals(interviewerSummary.getDate()))
        .findFirst().orElse(null);

    assertTrue(passedDto != null && passedDto.getPassed() == 1);
    assertTrue(conductedDto != null && conductedDto.getConducted() == 1);
  }

  @Test
  void testFindEventsBetweenDateNoEventsFound() {
    LocalDate from = LocalDate.now().minusDays(1);
    LocalDate to = LocalDate.now().plusDays(1);

    testUser.setEvents(List.of());

    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));

    List<EventDto> events = userService.findEventsBetweenDate(userId, from, to);

    assertTrue(events.isEmpty());
  }

  @Test
  void testFindEventsBetweenDateWithEvents() {
    Event event1 = new Event();
    event1.setId(1L);
    event1.setStartTime(ZonedDateTime.now());
    event1.setHostId(2L);
    event1.setRoomLink("roomLink1");
    event1.setParticipantIds(Arrays.asList(3L, 4L));

    Event event2 = new Event();
    event2.setId(2L);
    event2.setStartTime(ZonedDateTime.now().plusDays(2));
    event2.setHostId(2L);
    event2.setRoomLink("roomLink2");
    event2.setParticipantIds(Arrays.asList(3L, 4L));

    testUser.setEvents(Arrays.asList(event1, event2));

    EventDto eventDto1 = EventDto.builder()
        .id(1L)
        .build();

    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(userRepository.findAllById(any())).thenReturn(List.of(testUser));
    when(dataMapper.toDto(any(), any(), any())).thenReturn(eventDto1);

    LocalDate from = LocalDate.now().minusDays(1);
    LocalDate to = LocalDate.now().plusDays(1);

    List<EventDto> events = userService.findEventsBetweenDate(userId, from, to);

    assertEquals(1, events.size());
    assertEquals(event1.getId(), events.getFirst().getId());
  }

  @Test
  void testFindEventsBetweenDateUserNotFound() {
    LocalDate from = LocalDate.now().minusDays(1);
    LocalDate to = LocalDate.now().plusDays(1);

    when(userRepository.findById(any())).thenThrow(new UserNotFoundException("User not found"));

    assertThrows(UserNotFoundException.class,
        () -> userService.findEventsBetweenDate(userId, from, to));
  }

  @Test
  void testFindEventsFromDateTimeNoEventsFound() {
    ZonedDateTime from = ZonedDateTime.now().minusDays(1);

    testUser.setEvents(List.of());

    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));

    List<EventDto> events = userService.findEventsFromDateTime(userId, from);

    assertTrue(events.isEmpty());
  }

  @Test
  void testFindEventsFromDateTimeWithEvents() {
    Event event1 = new Event();
    event1.setId(1L);
    event1.setStartTime(ZonedDateTime.now());
    event1.setHostId(2L);
    event1.setRoomLink("roomLink1");
    event1.setParticipantIds(Arrays.asList(3L, 4L));

    Event event2 = new Event();
    event2.setId(2L);
    event2.setStartTime(ZonedDateTime.now().plusDays(2));
    event2.setHostId(2L);
    event2.setRoomLink("roomLink2");
    event2.setParticipantIds(Arrays.asList(3L, 4L));

    testUser.setEvents(Arrays.asList(event1, event2));

    EventDto eventDto1 = EventDto.builder()
        .id(1L)
        .build();
    EventDto eventDto2 = EventDto.builder()
        .id(2L)
        .build();

    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(userRepository.findAllById(any())).thenReturn(List.of(testUser));
    when(dataMapper.toDto(event1, testUser, Collections.singletonList(testUser)))
        .thenReturn(eventDto1);
    when(dataMapper.toDto(event2, testUser, Collections.singletonList(testUser)))
        .thenReturn(eventDto2);

    ZonedDateTime from = ZonedDateTime.now().minusDays(1);

    List<EventDto> events = userService.findEventsFromDateTime(userId, from);

    assertEquals(2, events.size());
    assertEquals(event1.getId(), events.getFirst().getId());
    assertEquals(event2.getId(), events.getLast().getId());
  }

  @Test
  void testFindEventsFromDateTimeUserNotFound() {
    ZonedDateTime from = ZonedDateTime.now().minusDays(1);

    when(userRepository.findById(any())).thenThrow(new UserNotFoundException("User not found"));

    assertThrows(UserNotFoundException.class,
        () -> userService.findEventsFromDateTime(userId, from));
  }

  @Test
  void testCreateAndMatchInterviewRequest_MatchFoundSuccessfully() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toEntity(any(InterviewRequestDto.class))).thenReturn(interviewRequest);
    when(interviewRequestService.save(any(InterviewRequest.class))).thenReturn(interviewRequest);
    when(userSecurityService.findEmailByUserId(anyLong())).thenReturn(userEmail);
    when(interviewMatchingService.match(any(InterviewRequest.class))).thenReturn(
        Optional.of(interview));

    doNothing().when(notificationService).addInterviewScheduled(any(), any(), any());
    doNothing().when(emailService).sendInterviewScheduledEmail(any(), any(), any(), any(), any());

    userService.createAndMatchInterviewRequest(userId, interviewRequestDto);

    verify(userSecurityService, times(2)).findEmailByUserId(anyLong());
    verify(notificationService, times(2))
        .addInterviewScheduled(any(), any(), any());
    verify(emailService, times(2))
        .sendInterviewScheduledEmail(any(), any(), any(), any(), any());
  }

  @Test
  void testCreateAndMatchInterviewRequest_NoMatchFound() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.toEntity(any(InterviewRequestDto.class))).thenReturn(interviewRequest);
    when(interviewRequestService.save(any(InterviewRequest.class))).thenReturn(interviewRequest);
    when(interviewMatchingService.match(any(InterviewRequest.class)))
        .thenReturn(Optional.empty());

    userService.createAndMatchInterviewRequest(userId, interviewRequestDto);

    verify(userSecurityService, never()).findEmailByUserId(anyLong());
    verify(notificationService, never()).addInterviewScheduled(any(), any(), any());
    verify(emailService, never()).sendInterviewScheduledEmail(any(), any(), any(), any(), any());
  }

  @Test
  void testDeleteRejectedInterview_Then_NoMatchedInterviewForRejectedRequest_And_ActiveRequest() {
    when(interviewService.deleteRejectedInterview(anyLong())).thenReturn(interview);
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(userSecurityService.findEmailByUserId(anyLong())).thenReturn(userEmail);
    when(userSecurityService.findEmailByUserId(anyLong())).thenReturn(userEmail);
    when(interviewMatchingService.match(interviewRequest,
        List.of(interviewRequest.getUser()))).thenReturn(Optional.empty());

    doNothing().when(notificationService).addRejectInterview(any(), any(), any());
    doNothing().when(emailService).sendInterviewRejectionMessage(any(), any(), any(), any());
    doNothing().when(interviewRequestService).handleRejectedInterview(any(), any());

    userService.deleteRejectedInterview(userId, userId);

    verify(userSecurityService, times(1))
        .findEmailByUserId(anyLong());
    verify(notificationService, times(2))
        .addRejectInterview(any(), any(), any());
    verify(notificationService, never())
        .addInterviewScheduled(any(), any(), any());
    verify(emailService, never())
        .sendInterviewScheduledEmail(any(), any(), any(), any(), any());
  }

  @Test
  void testDeleteRejectedInterview_Then_MatchedInterviewForRejectedRequest_And_ActiveRequest() {
    when(interviewService.deleteRejectedInterview(anyLong())).thenReturn(interview);
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(userSecurityService.findEmailByUserId(anyLong())).thenReturn(userEmail);
    when(userSecurityService.findEmailByUserId(anyLong())).thenReturn(userEmail);
    when(interviewMatchingService.match(interviewRequest,
        List.of(interviewRequest.getUser()))).thenReturn(Optional.of(interview));

    doNothing().when(emailService).sendInterviewRejectionMessage(any(), any(), any(), any());
    doNothing().when(interviewRequestService).handleRejectedInterview(any(), any());
    doNothing().when(notificationService).addRejectInterview(any(), any(), any());
    doNothing().when(notificationService).addInterviewScheduled(any(), any(), any());
    doNothing().when(emailService).sendInterviewScheduledEmail(any(), any(), any(), any(), any());

    userService.deleteRejectedInterview(userId, userId);

    verify(userSecurityService, times(5))
        .findEmailByUserId(anyLong());
    verify(notificationService, times(2))
        .addRejectInterview(any(), any(), any());
    verify(notificationService, times(4))
        .addInterviewScheduled(any(), any(), any());
    verify(emailService, times(4))
        .sendInterviewScheduledEmail(any(), any(), any(), any(), any());

  }

  @Test
  void testDeleteRejectedInterview_Then_OnlyOneMatchedInterview() {
    when(interviewService.deleteRejectedInterview(anyLong())).thenReturn(interview);
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(userSecurityService.findEmailByUserId(anyLong())).thenReturn(userEmail);
    when(userSecurityService.findEmailByUserId(anyLong())).thenReturn(userEmail);
    when(interviewMatchingService.match(interviewRequest,
        List.of(interviewRequest.getUser()))).thenReturn(Optional.empty())
        .thenReturn(Optional.of(interview));

    doNothing().when(emailService).sendInterviewRejectionMessage(any(), any(), any(), any());
    doNothing().when(interviewRequestService).handleRejectedInterview(any(), any());
    doNothing().when(notificationService).addRejectInterview(any(), any(), any());
    doNothing().when(notificationService).addInterviewScheduled(any(), any(), any());
    doNothing().when(emailService).sendInterviewScheduledEmail(any(), any(), any(), any(), any());

    userService.deleteRejectedInterview(userId, userId);

    verify(userSecurityService, times(3))
        .findEmailByUserId(anyLong());
    verify(notificationService, times(2))
        .addRejectInterview(any(), any(), any());
    verify(notificationService, times(2))
        .addInterviewScheduled(any(), any(), any());
    verify(emailService, times(2))
        .sendInterviewScheduledEmail(any(), any(), any(), any(), any());
  }
}