package com.ratifire.devrate.service.user;

import static com.ratifire.devrate.util.interview.DateTimeUtils.convertToUtcTimeZone;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.dto.BookmarkDto;
import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.EventDto;
import com.ratifire.devrate.dto.EventDto.Participant;
import com.ratifire.devrate.dto.InterviewRequestDto;
import com.ratifire.devrate.dto.InterviewStatsConductedPassedByDateDto;
import com.ratifire.devrate.dto.InterviewSummaryDto;
import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.dto.UserMainMasterySkillDto;
import com.ratifire.devrate.dto.UserPictureDto;
import com.ratifire.devrate.entity.Achievement;
import com.ratifire.devrate.entity.Bookmark;
import com.ratifire.devrate.entity.Contact;
import com.ratifire.devrate.entity.Education;
import com.ratifire.devrate.entity.EmploymentRecord;
import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.InterviewSummary;
import com.ratifire.devrate.entity.LanguageProficiency;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.exception.InterviewSummaryNotFoundException;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.InterviewSummaryRepository;
import com.ratifire.devrate.repository.SpecializationRepository;
import com.ratifire.devrate.repository.UserRepository;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.email.EmailService;
import com.ratifire.devrate.service.interview.InterviewMatchingService;
import com.ratifire.devrate.service.interview.InterviewRequestService;
import com.ratifire.devrate.service.interview.InterviewService;
import com.ratifire.devrate.service.specialization.SpecializationService;
import com.ratifire.devrate.util.interview.DateTimeUtils;
import com.ratifire.devrate.util.interview.InterviewPair;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for performing operations related to {@link User} entities.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private UserRepository userRepository;
  private final SpecializationRepository specializationRepository;
  private final InterviewSummaryRepository interviewSummaryRepository;
  private final SpecializationService specializationService;
  private final InterviewMatchingService interviewMatchingService;
  private final InterviewRequestService interviewRequestService;
  private final InterviewService interviewService;
  private final UserSecurityService userSecurityService;
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
  public User create(UserDto userDto) {
    User user = userMapper.toEntity(userDto);
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
  private User findUserById(long id) {
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
   * Counts the number of conducted and passed interviews for the specified
   * user within a given date range.
   *
   * @param userId the ID of the user
   * @param from the start date of the date range (inclusive)
   * @param to the end date of the date range (inclusive)
   * @return a list of InterviewConductedPassedDto objects with the count
   *         of conducted and passed interviews per date
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
    specializationService.checkIsMainAndSpecializationNameAlreadyExist(specializationDto, userId);
    User user = findUserById(userId);
    Specialization specialization = specializationDataMapper.toEntity(specializationDto);
    specialization.setUser(user);
    user.getSpecializations().add(specialization);
    updateUser(user);
    specializationService.createMasteriesForSpecialization(specialization.getId());
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
   * Sends alerts to both the interviewer and the candidate about the scheduled interview.
   *
   * @param interview the interview whose participants are sent alerts
   */
  private void sendInterviewScheduledAlerts(Interview interview) {
    ZonedDateTime interviewStartTimeInUtc = convertToUtcTimeZone(interview.getStartTime());
    InterviewRequest interviewer = interview.getInterviewerRequest();
    InterviewRequest candidate = interview.getCandidateRequest();

    notifyParticipant(candidate, interviewer, interviewStartTimeInUtc);
    notifyParticipant(interviewer, candidate, interviewStartTimeInUtc);
  }

  /**
   * Sends a notification and an email to a participant of an interview about the scheduled
   * interview.
   *
   * @param recipientRequest         the interview request of the recipient of the notification
   * @param secondParticipantRequest the interview request of the other participant in the
   *                                 interview
   * @param interviewStartTimeInUtc  the start time of the interview in UTC
   */
  private void notifyParticipant(InterviewRequest recipientRequest,
      InterviewRequest secondParticipantRequest, ZonedDateTime interviewStartTimeInUtc) {

    User recipient = recipientRequest.getUser();
    String recipientEmail = userSecurityService.findEmailByUserId(recipient.getId());
    String role = String.valueOf(recipientRequest.getRole());

    notificationService.addInterviewScheduled(recipient, role,
        interviewStartTimeInUtc);

    emailService.sendInterviewScheduledEmail(recipient, recipientEmail,
        interviewStartTimeInUtc, secondParticipantRequest);
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
    notificationService.rejectInterview(recipient, rejector.getFirstName(),
        scheduledTime);
    notificationService.rejectInterview(rejector, recipient.getFirstName(),
        scheduledTime);

    String recipientEmail = userSecurityService.findEmailByUserId(recipient.getId());
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
        .map(this::constructEventDto)
        .toList();
  }

  /**
   * Constructs an {@link EventDto} from a given {@link Event} entity.
   *
   * @param event the {@link Event} entity to be converted
   * @return an {@link EventDto} object that represents the given event
   */
  private EventDto constructEventDto(Event event) {
    Participant hostEvent = createParticipant(event.getHostId(), InterviewRequestRole.INTERVIEWER);

    List<Participant> participants = event.getParticipantIds().stream()
        .map(participantId ->
            createParticipant(participantId, InterviewRequestRole.CANDIDATE))
        .toList();

    return EventDto.builder()
        .id(event.getId())
        .relatedId(event.getRelatedId())
        .zoomLink(event.getZoomLink())
        .host(hostEvent)
        .participants(participants)
        .startTime(event.getStartTime())
        .build();
  }

  /**
   * Creates a {@link Participant} object based on the given user ID and role.
   *
   * @param userId the ID of the user to be converted to a {@link Participant}
   * @param role   the role of the participant in the event
   * @return a {@link Participant} object that represents the user with the given ID and role
   */
  private Participant createParticipant(long userId, InterviewRequestRole role) {
    try {
      User host = findUserById(userId);

      return Participant.builder()
          .name(host.getFirstName())
          .surname(host.getLastName())
          .role(role)
          .build();
    } catch (UserNotFoundException ex) {
      return new Participant();
    }
  }
}
