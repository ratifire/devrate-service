package com.ratifire.devrate.service.user;

import static com.ratifire.devrate.util.interview.DateTimeUtils.convertToUtcTimeZone;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.dto.BookmarkDto;
import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.EventDto;
import com.ratifire.devrate.dto.InterviewFeedbackDto;
import com.ratifire.devrate.dto.InterviewRequestDto;
import com.ratifire.devrate.dto.InterviewStatsConductedPassedByDateDto;
import com.ratifire.devrate.dto.InterviewSummaryDto;
import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.dto.SkillFeedbackDto;
import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.dto.UserMainHardSkillsDto;
import com.ratifire.devrate.dto.UserMainMasterySkillDto;
import com.ratifire.devrate.dto.UserNameSearchDto;
import com.ratifire.devrate.dto.UserPictureDto;
import com.ratifire.devrate.entity.Achievement;
import com.ratifire.devrate.entity.Bookmark;
import com.ratifire.devrate.entity.Contact;
import com.ratifire.devrate.entity.Education;
import com.ratifire.devrate.entity.EmploymentRecord;
import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.InterviewSummary;
import com.ratifire.devrate.entity.LanguageProficiency;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewFeedbackDetail;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.exception.InterviewSummaryNotFoundException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.exception.UserSearchInvalidInputException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.InterviewSummaryRepository;
import com.ratifire.devrate.repository.SpecializationRepository;
import com.ratifire.devrate.repository.UserRepository;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.email.EmailService;
import com.ratifire.devrate.service.interview.InterviewFeedbackDetailService;
import com.ratifire.devrate.service.interview.InterviewMatchingService;
import com.ratifire.devrate.service.interview.InterviewRequestService;
import com.ratifire.devrate.service.interview.InterviewService;
import com.ratifire.devrate.service.interview.InterviewSummaryService;
import com.ratifire.devrate.service.specialization.MasteryService;
import com.ratifire.devrate.service.specialization.SkillService;
import com.ratifire.devrate.service.specialization.SpecializationService;
import com.ratifire.devrate.util.interview.DateTimeUtils;
import com.ratifire.devrate.util.interview.InterviewPair;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for performing operations related to {@link User} entities.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private static final String NAME_PATTERN = "^[\\p{L}\\s\\-']+$";
  private static final Pattern NAME_REGEX = Pattern.compile(NAME_PATTERN);

  private UserRepository userRepository;
  private final SpecializationRepository specializationRepository;
  private final InterviewSummaryRepository interviewSummaryRepository;
  private final SpecializationService specializationService;
  private final MasteryService masteryService;
  private final SkillService skillService;
  private final InterviewMatchingService interviewMatchingService;
  private final InterviewRequestService interviewRequestService;
  private final InterviewService interviewService;
  private final InterviewSummaryService interviewSummaryService;
  private final InterviewFeedbackDetailService interviewFeedbackDetailService;
  private final EmailService emailService;
  private final NotificationService notificationService;
  private final DataMapper<UserDto, User> userMapper;
  private final DataMapper<ContactDto, Contact> contactMapper;
  private final DataMapper<EducationDto, Education> educationMapper;
  private final DataMapper<AchievementDto, Achievement> achievementMapper;
  private final DataMapper<EmploymentRecordDto, EmploymentRecord> employmentRecordMapper;
  private final DataMapper<LanguageProficiencyDto, LanguageProficiency> languageProficiencyMapper;
  private final DataMapper<BookmarkDto, Bookmark> bookmarkMapper;
  private final DataMapper<InterviewSummaryDto, InterviewSummary> interviewSummaryMapper;
  private final DataMapper<SpecializationDto, Specialization> specializationDataMapper;
  private final DataMapper<UserMainMasterySkillDto, Specialization> userMainMasterySkillMapper;
  private final DataMapper<InterviewRequestDto, InterviewRequest> interviewRequestMapper;
  private final DataMapper<EventDto, Event> eventMapper;
  private final DataMapper<UserMainHardSkillsDto, Specialization> userMainHardSkillsMapper;

  @Autowired
  public void setUserRepository(@Lazy UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Retrieves a user by ID.
   *
   * @param id the ID of the user to retrieve
   * @return the user DTO
   * @throws UserNotFoundException if the user with the specified ID is not found
   */
  public UserDto findById(long id) {
    UserDto dto = userMapper.toDto(findUserById(id));

    specializationRepository.findSpecializationByUserIdAndMainTrue(id)
        .ifPresent(spec -> {
          dto.setHardSkillMark(
              spec.getMainMastery() != null ? spec.getMainMastery().getHardSkillMark()
                  : BigDecimal.ZERO);
          dto.setSoftSkillMark(
              spec.getMainMastery() != null ? spec.getMainMastery().getSoftSkillMark()
                  : BigDecimal.ZERO);
        });

    return dto;
  }

  /**
   * Creates user.
   *
   * @param userDto the user as a DTO
   * @return the created user
   */
  public User create(UserDto userDto, String email, String password) {
    User user = userMapper.toEntity(userDto);
    user.setEmail(email);
    user.setPassword(password);
    return userRepository.save(user);
  }

  /**
   * Updates user.
   *
   * @param userDto the updated user as a DTO
   * @return the updated user as a DTO
   * @throws UserNotFoundException if the user does not exist
   */
  public UserDto update(UserDto userDto) {
    User user = findUserById(userDto.getId());
    userMapper.updateEntity(userDto, user);
    return userMapper.toDto(userRepository.save(user));
  }

  /**
   * Updates an existing user entity.
   *
   * @param user the updated user entity
   * @return the updated user entity
   */
  public User updateUser(User user) {
    return userRepository.save(user);
  }

  /**
   * Deletes user by ID.
   *
   * @param userId the ID of the user
   * @throws UserNotFoundException if the user does not exist
   */
  public void delete(long userId) {
    User user = findUserById(userId);
    userRepository.delete(user);
  }

  /**
   * Refreshes the interview counts for a list of users.
   *
   * @param users the list of users for whom the interview counts need to be refreshed. Each user's
   *              conducted and completed interview counts will be recalculated and updated in the
   *              database.
   */
  public void refreshUserInterviewCounts(List<User> users) {
    users.forEach(user -> {
      long userId = user.getId();
      user.setConductedInterviews(interviewSummaryRepository.countByInterviewerId(userId));
      user.setCompletedInterviews(interviewSummaryRepository.countByCandidateId(userId));
      updateUser(user);
    });
  }

  /**
   * Retrieves all language proficiencies associated with the user.
   *
   * @param userId the ID of the user to associate the language proficiencies with
   * @return A list of LanguageProficiencyDto objects.
   */
  public List<LanguageProficiencyDto> findAllLanguageProficienciesByUserId(long userId) {
    User user = findUserById(userId);
    return languageProficiencyMapper.toDto(user.getLanguageProficiencies());
  }

  /**
   * Saves new language proficiencies for a user identified by userId.
   *
   * @param userId                  the ID of the user to whom the language proficiency belongs
   * @param languageProficiencyDtos the language proficiency information to save
   * @return the list of saved LanguageProficiencyDto objects
   */
  public List<LanguageProficiencyDto> saveLanguageProficiencies(long userId,
      List<LanguageProficiencyDto> languageProficiencyDtos) {
    User user = findUserById(userId);
    List<LanguageProficiency> existingProficiencies = user.getLanguageProficiencies();

    existingProficiencies.removeIf(proficiency -> languageProficiencyDtos.stream()
        .noneMatch(proficiencyDto -> proficiencyDto.getName().equals(proficiency.getName())));

    for (LanguageProficiencyDto languageProficiencyDto : languageProficiencyDtos) {
      Optional<LanguageProficiency> languageProficiency = existingProficiencies.stream()
          .filter(proficiency -> proficiency.getName().equals(languageProficiencyDto.getName()))
          .findFirst();

      if (languageProficiency.isPresent()) {
        languageProficiencyMapper.updateEntity(languageProficiencyDto, languageProficiency.get());
      } else {
        existingProficiencies.add(languageProficiencyMapper.toEntity(languageProficiencyDto));
      }
    }
    userRepository.save(user);
    return languageProficiencyMapper.toDto(user.getLanguageProficiencies());
  }

  /**
   * Retrieves a user entity by ID.
   *
   * @param id the ID of the user to retrieve
   * @return the user entity
   * @throws UserNotFoundException if the user with the specified ID is not found
   */
  public User findUserById(long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("The user not found with id " + id));
  }

  /**
   * Retrieves EmploymentRecord (work experience) information by user ID.
   *
   * @param userId the ID of the user
   * @return the user's work experience as a DTO
   */
  public List<EmploymentRecordDto> getEmploymentRecordsByUserId(long userId) {
    User user = findUserById(userId);
    return employmentRecordMapper.toDto(user.getEmploymentRecords());
  }

  /**
   * Creates EmploymentRecord information.
   *
   * @param employmentRecordDto the user's EmploymentRecord information as a DTO
   * @return the created user work experience information as a DTO
   */
  public EmploymentRecordDto createEmploymentRecord(EmploymentRecordDto employmentRecordDto,
      long userId) {
    User user = findUserById(userId);
    EmploymentRecord employmentRecord = employmentRecordMapper.toEntity(employmentRecordDto);
    user.getEmploymentRecords().add(employmentRecord);
    updateUser(user);
    return employmentRecordMapper.toDto(employmentRecord);
  }

  /**
   * Retrieves the picture associated with a user by their user ID.
   *
   * @param userId the ID of the user whose picture is to be retrieved
   * @return the user's picture as a base64-encoded string
   */
  public UserPictureDto getUserPicture(long userId) {
    return new UserPictureDto(userRepository.findPictureByUserId(userId));
  }

  /**
   * Adds or updates a user's picture by user ID. If the user already has a picture, it is
   * replaced.
   *
   * @param userId      the ID of the user whose picture is to be added or updated
   * @param userPicture the picture data as a base64-encoded string
   */
  public void addUserPicture(long userId, String userPicture) {
    User user = findUserById(userId);
    user.setPicture(userPicture);
    updateUser(user);
  }

  /**
   * Removes a user's picture by user ID.
   *
   * @param userId the ID of the user whose picture is to be removed
   */
  public void deleteUserPicture(long userId) {
    User user = findUserById(userId);
    user.setPicture(null);
    updateUser(user);
  }

  /**
   * Retrieves a list of achievements for a specific user by their ID.
   *
   * @param userId The ID of the user whose achievements are to be retrieved.
   * @return A list of AchievementDto objects representing the achievements of the user.
   */
  public List<AchievementDto> getAchievementsByUserId(long userId) {
    User user = findUserById(userId);
    return achievementMapper.toDto(user.getAchievements());
  }

  /**
   * Creates a new achievement for a specific user.
   *
   * @param userId         The ID of the user for whom the achievement is to be created.
   * @param achievementDto The AchievementDto object containing details of the achievement to be
   *                       created.
   * @return The AchievementDto object representing the created achievement.
   */
  public AchievementDto createAchievement(long userId, AchievementDto achievementDto) {
    User user = findUserById(userId);
    Achievement achievement = achievementMapper.toEntity(achievementDto);
    user.getAchievements().add(achievement);
    updateUser(user);
    return achievementMapper.toDto(achievement);
  }

  /**
   * Retrieves a list of education details for a specific user identified by their user ID.
   *
   * @param userId The unique identifier of the user.
   * @return A list of {@link EducationDto} objects representing the education details.
   */
  public List<EducationDto> getEducationsByUserId(long userId) {
    User user = findUserById(userId);
    return educationMapper.toDto(user.getEducations());
  }

  /**
   * Creates a new education record for the specified user.
   *
   * @param userId       The unique identifier of the user for whom the education record is
   *                     created.
   * @param educationDto The {@link EducationDto} object containing the details of the education to
   *                     be created.
   * @return The {@link EducationDto} object representing the newly created education record.
   */
  public EducationDto createEducation(long userId, EducationDto educationDto) {
    User user = findUserById(userId);
    Education education = educationMapper.toEntity(educationDto);
    user.getEducations().add(education);
    updateUser(user);
    return educationMapper.toDto(education);
  }

  /**
   * Retrieves all contacts associated with the user.
   *
   * @param userId the ID of the user to associate the contacts with
   * @return A list of ContactDto objects.
   */
  public List<ContactDto> findAllContactsByUserId(long userId) {
    User user = findUserById(userId);
    return contactMapper.toDto(user.getContacts());
  }

  /**
   * Saves contacts for a user identified by userId.
   *
   * @param userId      the ID of the user to whom the contacts belongs
   * @param contactDtos the contact information to save
   * @return the list of saved ContactDto objects
   */
  public List<ContactDto> saveContacts(long userId, List<ContactDto> contactDtos) {
    User user = findUserById(userId);
    List<Contact> existingContacts = user.getContacts();

    existingContacts.removeIf(contact -> contactDtos.stream()
        .noneMatch(contactDto -> contactDto.getType().equals(contact.getType())));

    for (ContactDto contactDto : contactDtos) {
      Optional<Contact> contact = existingContacts.stream()
          .filter(c -> c.getType().equals(contactDto.getType()))
          .findFirst();

      if (contact.isPresent()) {
        contactMapper.updateEntity(contactDto, contact.get());
      } else {
        existingContacts.add(contactMapper.toEntity(contactDto));
      }
    }
    userRepository.save(user);
    return contactMapper.toDto(user.getContacts());
  }

  /**
   * Retrieves all bookmarks associated with the user.
   *
   * @param userId the ID of the user to associate the bookmarks with
   * @return A list of BookmarkDto objects.
   */
  public List<BookmarkDto> getBookmarksByUserId(long userId) {
    User user = findUserById(userId);
    return bookmarkMapper.toDto(user.getBookmarks());
  }

  /**
   * Creates a new bookmark for a specific user.
   *
   * @param userId      The ID of the user for whom the bookmark is to be created.
   * @param bookmarkDto The BookmarkDto object containing details of the bookmark to be created.
   */
  public void createBookmark(long userId, BookmarkDto bookmarkDto) {
    User user = findUserById(userId);
    Bookmark bookmark = bookmarkMapper.toEntity(bookmarkDto);
    user.getBookmarks().add(bookmark);
    updateUser(user);
  }

  /**
   * Retrieves all interview summaries associated with the user.
   *
   * @param userId the ID of the user to associate the interview summaries with
   * @return A list of InterviewSummaryDto objects.
   */
  public List<InterviewSummaryDto> getInterviewSummariesByUserId(long userId) {
    User user = findUserById(userId);
    return interviewSummaryMapper.toDto(user.getInterviewSummaries());
  }

  /**
   * Counts the number of conducted and passed interviews for the specified user within a given date
   * range.
   *
   * @param userId the ID of the user
   * @param from   the start date of the date range (inclusive)
   * @param to     the end date of the date range (inclusive)
   * @return a list of InterviewConductedPassedDto objects with the count of conducted and passed
   *         interviews per date
   */
  public List<InterviewStatsConductedPassedByDateDto> getInterviewStatConductedPassedByDate(
      long userId, LocalDate from, LocalDate to) {
    List<InterviewSummary> interviewSummaries = interviewSummaryRepository
        .findByCandidateOrInterviewerAndDateBetween(userId, from, to);

    Map<LocalDate, InterviewStatsConductedPassedByDateDto> interviewStats = new HashMap<>();

    for (InterviewSummary summary : interviewSummaries) {
      LocalDate date = summary.getDate();
      interviewStats.putIfAbsent(date, new InterviewStatsConductedPassedByDateDto(date, 0, 0));

      if (summary.getCandidateId() == userId) {
        interviewStats.get(date).setPassed(interviewStats.get(date).getPassed() + 1);
      }

      if (summary.getInterviewerId() == userId) {
        interviewStats.get(date).setConducted(interviewStats.get(date).getConducted() + 1);
      }
    }

    return interviewStats.values().stream()
        .sorted(Comparator.comparing(InterviewStatsConductedPassedByDateDto::getDate))
        .toList();
  }

  /**
   * Deletes the association between a user and an interview summary.
   *
   * @param userId the ID of the user whose association with the interview summary is to be deleted
   * @param id     the ID of the interview summary to be removed from the user's associations
   */
  public void deleteInterviewSummary(long userId, long id) {
    User user = findUserById(userId);
    InterviewSummary interviewSummary = interviewSummaryRepository.findById(id)
        .orElseThrow(() -> new InterviewSummaryNotFoundException(id));
    user.getInterviewSummaries().remove(interviewSummary);
    userRepository.save(user);
  }

  /**
   * Retrieves specialization by user ID.
   *
   * @param userId the ID of the user
   * @return the user's specialization as a DTO
   */
  public List<SpecializationDto> getSpecializationsByUserId(long userId) {
    User user = findUserById(userId);
    return specializationDataMapper.toDto(user.getSpecializations());
  }

  /**
   * Retrieves a list of all main mastery skills for the specified user.
   *
   * @param userId the ID of the user whose mastery skills are to be retrieved.
   * @return a list of all main mastery skills for the user.
   */
  public List<UserMainMasterySkillDto> getPrivateMainMasterySkillsByUserId(long userId) {
    User user = findUserById(userId);
    return userMainMasterySkillMapper.toDto(user.getSpecializations());
  }

  /**
   * Retrieves a list of all main mastery skills for the specified user, excluding hidden skills.
   *
   * @param userId the ID of the user whose mastery skills are to be retrieved.
   * @return a list of main mastery skills for the user, excluding hidden skills.
   */
  public List<UserMainMasterySkillDto> getPublicMainMasterySkillsByUserId(long userId) {
    User user = findUserById(userId);
    user.getSpecializations().forEach(specialization -> {
      List<Skill> skills = specialization.getMainMastery().getSkills().stream()
          .filter(skill -> !skill.isHidden())
          .toList();
      specialization.getMainMastery().setSkills(skills);
    });
    return userMainMasterySkillMapper.toDto(user.getSpecializations());
  }

  /**
   * Creates specialization information. Сreate masteries for specialization.
   *
   * @param specializationDto the user's specialization information as a DTO
   * @return the created user specialization information as a DTO
   */
  @Transactional
  public SpecializationDto createSpecialization(SpecializationDto specializationDto,
      long userId) {
    specializationService.validateBeforeCreate(specializationDto, userId);
    User user = findUserById(userId);
    Specialization specialization = specializationDataMapper.toEntity(specializationDto);
    specialization.setUser(user);
    user.getSpecializations().add(specialization);
    updateUser(user);
    specializationService.createMasteriesForSpecialization(specialization,
        specializationDto.getMainMasteryName());
    return specializationDataMapper.toDto(specialization);
  }

  /**
   * Creates an interview request for the specified user and attempts to match it with an existing
   * request.
   *
   * @param userId     the ID of the user creating the interview request
   * @param requestDto the DTO containing the interview request details
   */
  @Transactional
  public void createAndMatchInterviewRequest(long userId, InterviewRequestDto requestDto) {
    InterviewRequest interviewRequest = createInterviewRequest(userId, requestDto);
    Optional<Interview> interview = interviewMatchingService.match(interviewRequest);
    interview.ifPresent(this::sendInterviewScheduledAlerts);
  }

  /**
   * Retrieves the interview request for the specified user, role and mastery id.
   *
   * @param userId    the user's ID
   * @param role      the role of the interview request
   * @param masteryId the mastery id of the interview request
   * @return the interview request as InterviewRequestDto
   */
  public InterviewRequestDto getInterviewRequest(long userId, InterviewRequestRole role,
      long masteryId) {
    InterviewRequest interviewRequest =
        interviewRequestService.findByUserIdRoleMasteryId(userId, role, masteryId);
    return interviewRequestMapper.toDto(interviewRequest);
  }

  /**
   * Updates and matches the interview request for the specified user.
   *
   * @param userId     the user's ID
   * @param requestDto the interview request details
   */
  @Transactional
  public void updateAndMatchInterviewRequest(long userId, InterviewRequestDto requestDto) {
    InterviewRequest interviewRequest = updateInterviewRequest(userId, requestDto);
    if (interviewRequest.isActive()) {
      Optional<Interview> interview = interviewMatchingService.match(interviewRequest);
      interview.ifPresent(this::sendInterviewScheduledAlerts);
    }
  }

  /**
   * Deletes an interview request by its ID.
   *
   * @param requestId the ID of the interview request to be deleted
   */
  public void deleteInterviewRequest(long requestId) {
    interviewRequestService.deleteInterviewRequestById(requestId);
  }

  /**
   * Sends alerts to both the interviewer and the candidate about the scheduled interview.
   *
   * @param interview the interview whose participants are sent alerts
   */
  private void sendInterviewScheduledAlerts(Interview interview) {
    ZonedDateTime interviewStartTimeInUtc = convertToUtcTimeZone(interview.getStartTime());
    InterviewRequest interviewer = interview.getInterviewerRequest();
    InterviewRequest candidate = interview.getCandidateRequest();

    notifyParticipant(candidate, interviewer, interviewStartTimeInUtc, interview.getZoomJoinUrl());
    notifyParticipant(interviewer, candidate, interviewStartTimeInUtc, interview.getZoomJoinUrl());
  }

  /**
   * Sends a notification and an email to a participant of an interview about the scheduled
   * interview.
   *
   * @param recipientRequest         the interview request of the recipient of the notification
   * @param secondParticipantRequest the interview request of the other participant in the
   *                                 interview
   * @param interviewStartTimeInUtc  the start time of the interview in UTC
   * @param zoomJoinUrl              the join url to the zoom meeting
   */
  private void notifyParticipant(InterviewRequest recipientRequest,
      InterviewRequest secondParticipantRequest, ZonedDateTime interviewStartTimeInUtc,
      String zoomJoinUrl) {

    User recipient = recipientRequest.getUser();
    String recipientEmail = recipient.getEmail();
    String role = String.valueOf(recipientRequest.getRole());

    notificationService.addInterviewScheduled(recipient, role,
        interviewStartTimeInUtc, recipientEmail);

    emailService.sendInterviewScheduledEmail(recipient, recipientEmail,
        interviewStartTimeInUtc, secondParticipantRequest, zoomJoinUrl);
  }

  /**
   * Creates an interview request for the specified user.
   *
   * @param userId     the ID of the user creating the interview request
   * @param requestDto the DTO containing the interview request details
   * @return the created InterviewRequest entity
   */
  private InterviewRequest createInterviewRequest(long userId, InterviewRequestDto requestDto) {
    User user = findUserById(userId);
    InterviewRequest interviewRequest = interviewRequestMapper.toEntity(requestDto);
    interviewRequest.setUser(user);
    return interviewRequestService.save(interviewRequest);
  }

  /**
   * Updates the interview request for the specified user based on the provided request DTO.
   *
   * @param userId     the user's ID
   * @param requestDto the interview request details
   * @return the updated InterviewRequest entity
   */
  private InterviewRequest updateInterviewRequest(long userId, InterviewRequestDto requestDto) {
    InterviewRequest interviewRequest = interviewRequestService.findByUserIdRoleMasteryId(
        userId, requestDto.getRole(), requestDto.getMasteryId());
    interviewRequestMapper.updateEntity(requestDto, interviewRequest);
    return interviewRequestService.save(interviewRequest);
  }

  /**
   * Delete the interview that was rejected by the user.
   *
   * @param userId      the ID of the user who rejected the interview
   * @param interviewId the rejected interview ID
   */
  @Transactional
  public void deleteRejectedInterview(long userId, long interviewId) {
    Interview interview = interviewService.deleteRejectedInterview(interviewId);

    User user = findUserById(userId);

    InterviewPair<InterviewRequest, InterviewRequest> activeAndRejectedRequest =
        determineActiveAndRejectedRequest(user, interview);

    InterviewRequest activeRequest = activeAndRejectedRequest.getCandidate();
    InterviewRequest rejectedRequest = activeAndRejectedRequest.getInterviewer();

    notifyUsers(activeRequest.getUser(), user, interview.getStartTime());
    interviewRequestService.handleRejectedInterview(activeRequest, rejectedRequest);

    Optional<Interview> matchedInterviewForActiveRequest = interviewMatchingService.match(
        activeRequest, List.of(rejectedRequest.getUser()));
    matchedInterviewForActiveRequest.ifPresent(this::sendInterviewScheduledAlerts);
    Optional<Interview> matchedInterviewForRejectedRequest = interviewMatchingService.match(
        rejectedRequest, List.of(activeRequest.getUser()));
    matchedInterviewForRejectedRequest.ifPresent(this::sendInterviewScheduledAlerts);
  }

  /**
   * Determines the active and rejected interview requests based on the user who rejected.
   *
   * @param user      The user who rejected the interview.
   * @param interview The interview to be processed.
   * @return An InterviewPair containing the active and rejected InterviewRequests.
   */
  private InterviewPair<InterviewRequest, InterviewRequest> determineActiveAndRejectedRequest(
      User user, Interview interview) {
    return isCandidateRejected(user, interview.getCandidateRequest())
        ? InterviewPair.<InterviewRequest, InterviewRequest>builder()
        .candidate(interview.getInterviewerRequest())    // interviewer has active request
        .interviewer(interview.getCandidateRequest())    // candidate has rejected request
        .build()
        : InterviewPair.<InterviewRequest, InterviewRequest>builder()
            .candidate(interview.getCandidateRequest())    // candidate has active request
            .interviewer(interview.getInterviewerRequest())    // interviewer has rejected request
            .build();
  }

  /**
   * Checks if the rejector is the candidate in the interview.
   *
   * @param rejector         The user who rejected the interview.
   * @param candidateRequest The interview request from the candidate.
   * @return true if the rejector is the candidate, false otherwise.
   */
  private boolean isCandidateRejected(User rejector, InterviewRequest candidateRequest) {
    return rejector.getId() == candidateRequest.getUser().getId();
  }

  /**
   * Notifies users involved in the interview rejection.
   *
   * @param recipient     The user for whom rejected the interview.
   * @param rejector      The user who rejected the interview.
   * @param scheduledTime The scheduled time of the interview.
   */
  private void notifyUsers(User recipient, User rejector,
      ZonedDateTime scheduledTime) {
    String recipientEmail = recipient.getEmail();
    notificationService.addRejectInterview(recipient, rejector.getFirstName(),
        scheduledTime, recipientEmail);
    String rejectorEmail = rejector.getEmail();
    notificationService.addRejectInterview(rejector, recipient.getFirstName(),
        scheduledTime, rejectorEmail);

    emailService.sendInterviewRejectionMessage(recipient, rejector, scheduledTime, recipientEmail);
  }

  /**
   * Retrieves a list of events for a specified user that occur within a given time range.
   *
   * @param userId the ID of the user whose events are to be retrieved
   * @param from   the start of the time range (inclusive)
   * @param to     the end of the time range (inclusive)
   * @return a list of {@link EventDto} objects representing the events for the user
   */
  public List<EventDto> findEventsBetweenDate(long userId, LocalDate from, LocalDate to) {
    User user = findUserById(userId);

    return user.getEvents().stream()
        .filter(event -> DateTimeUtils.isWithinRange(event.getStartTime().toLocalDate(), from, to))
        .map(event -> {
          User host = findUserById(event.getHostId());
          List<User> participants = userRepository.findAllById(event.getParticipantIds());
          return eventMapper.toDto(event, host, participants);
        })
        .toList();
  }

  /**
   * Retrieves a list of events for a given user that start from a specified date and time in UTC.
   *
   * @param userId the ID of the user whose events are to be retrieved
   * @param from   the starting date and time from which events should be retrieved
   * @return a list of {@link EventDto} objects representing the events starting from
   * @throws UserNotFoundException if no user with the specified ID is found
   */
  public List<EventDto> findEventsFromDateTime(long userId, ZonedDateTime from) {
    User user = findUserById(userId);

    return user.getEvents().stream()
        .filter(event -> !event.getStartTime().isBefore(convertToUtcTimeZone(from)))
        .sorted(Comparator.comparing(Event::getStartTime))
        .map(event -> {
          User host = findUserById(event.getHostId());
          List<User> participants = userRepository.findAllById(event.getParticipantIds());
          return eventMapper.toDto(event, host, participants);
        })
        .toList();
  }

  /**
   * Saves feedback provided by a reviewer and updates associated mastery marks.
   *
   * @param reviewerId           The ID of the reviewer submitting the feedback.
   * @param interviewFeedbackDto The  interviewFeedbackDto object containing all the feedback
   *                             details
   */
  @Transactional
  public void saveFeedback(long reviewerId, InterviewFeedbackDto interviewFeedbackDto) {
    InterviewFeedbackDetail feedbackDetail = interviewFeedbackDetailService.findDetailById(
        interviewFeedbackDto.getInterviewFeedbackDetailId());

    if (!new HashSet<>(feedbackDetail.getSkillsIds())
        .equals(interviewFeedbackDto.getSkills().stream()
            .map(SkillFeedbackDto::getId)
            .collect(Collectors.toSet()))) {
      throw new ResourceNotFoundException("Input invalid skills");
    }

    InterviewRequestRole reviewerRole = interviewSummaryService.addComment(
        feedbackDetail.getInterviewSummaryId(), reviewerId, interviewFeedbackDto.getComment());

    skillService.updateSkillMarksAfterGettingFeedback(interviewFeedbackDto.getSkills());

    masteryService.updateMasteryAverageMarksAfterGettingFeedback(
        feedbackDetail.getEvaluatedMasteryId(), reviewerRole);

    interviewFeedbackDetailService.deleteById(feedbackDetail.getId());
  }

  /**
   * Retrieves the main hard skills of a user.
   *
   * @param userId the ID of the user whose main hard skills are being retrieved
   * @return a list of {@link UserMainHardSkillsDto} representing the user's main hard skills
   * @throws UserNotFoundException if no user with the given ID is found
   */
  public List<UserMainHardSkillsDto> getMainHardSkills(long userId) {
    User user = findUserById(userId);
    return userMainHardSkillsMapper.toDto(user.getSpecializations());
  }

  /**
   * Searches for users based on a provided query string. The query can contain either a first name,
   * a last name, or both separated by a space. If both names are provided, the method attempts to
   * find users that match both.
   *
   * @param query the search query containing the user's first name, last name, or both.
   * @return a list of {@link UserNameSearchDto} containing the search results.
   */
  public List<UserNameSearchDto> searchUsers(String query) {
    if (query == null || query.isBlank()) {
      return List.of();
    }

    String[] parts = query.trim().split("\\s+");
    String firstParam = parts[0];
    String secondParam = parts.length > 1 ? parts[1] : "";

    if (isNameInvalid(firstParam) || (!secondParam.isBlank() && isNameInvalid(secondParam))) {
      throw new UserSearchInvalidInputException("Please enter a valid first/last name using only "
          + "letters, spaces, hyphens, or apostrophes.");
    }

    Pageable pageable = PageRequest.of(0, 10);
    return userRepository.findUsersByName(firstParam, secondParam, query, pageable);
  }

  /**
   * Checks if a given name is invalid based on the predefined regex pattern.
   *
   * @param name the name to validate.
   * @return true if the name does not match the pattern, false otherwise.
   */
  private boolean isNameInvalid(String name) {
    return !NAME_REGEX.matcher(name).matches();
  }

  /**
   * Retrieves a list of all notifications for a given user.
   *
   * @param userId the ID of the user whose notifications are to be retrieved
   * @return a list of {@link NotificationDto} representing the user's notifications
   */
  public List<NotificationDto> getNotificationsByUserId(long userId) {
    return notificationService.getAllByUserId(userId);
  }

  /**
   * Sends a test notification to a user.
   */
  // TODO: ATTENTION!!! Remove this method after testing is completed.
  public void sendTestNotification(long userId, NotificationDto notificationDto) {
    String userEmail = findEmailByUserId(userId);
    User user = findUserById(userId);
    Notification notification = Notification.builder()
        .user(user)
        .read(notificationDto.isRead())
        .payload(notificationDto.getPayload())
        .type(notificationDto.getType())
        .createdAt(notificationDto.getCreatedAt())
        .build();
    notificationService.sendTestNotification(userEmail, notification);
  }

  /**
   * Finds the email address associated with a given user ID.
   *
   * @param userId the ID of the user whose email address is to be retrieved
   * @return the email address of the user with the specified ID
   */
  public String findEmailByUserId(long userId) {
    return userRepository.findEmailByUserId(userId);
  }

  public User findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }
}
