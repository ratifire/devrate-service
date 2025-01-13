package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.dto.BookmarkDto;
import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.EventDto;
import com.ratifire.devrate.dto.InterviewFeedbackDto;
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
import com.ratifire.devrate.entity.interview.InterviewFeedbackDetail;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.exception.InterviewSummaryNotFoundException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.exception.UserSearchInvalidInputException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.InterviewSummaryRepository;
import com.ratifire.devrate.repository.SpecializationRepository;
import com.ratifire.devrate.repository.UserRepository;
import com.ratifire.devrate.service.interview.InterviewFeedbackDetailService;
import com.ratifire.devrate.service.interview.InterviewSummaryService;
import com.ratifire.devrate.util.DateTimeUtils;
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
  private final InterviewSummaryService interviewSummaryService;
  private final InterviewFeedbackDetailService interviewFeedbackDetailService;
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
  private final DataMapper<EventDto, Event> eventMapper;
  private final DataMapper<UserMainHardSkillsDto, Specialization> userMainHardSkillsMapper;

  @Autowired
  public void setUserRepository(@Lazy UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Retrieves a user entity by ID.
   *
   * @param id the ID of the user to retrieve
   * @return the user entity
   * @throws UserNotFoundException if the user with the specified ID is not found
   */
  public User findById(long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  public List<User> findByIds(List<Long> ids) {
    return userRepository.findByIds(ids);
  }

  /**
   * Retrieves a User entity by their email address.
   *
   * @param email the email address of the user to retrieve
   * @return the User entity associated with the given email address
   */
  public User findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  /**
   * Checks if a user exists with the specified email address.
   *
   * @param email the email address to check for existence
   * @return true if a user with the given email exists
   */
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
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

  /**
   * Retrieves a user by ID.
   *
   * @param id the ID of the user to retrieve
   * @return the user DTO
   * @throws UserNotFoundException if the user with the specified ID is not found
   */
  public UserDto getDtoById(long id) {
    UserDto dto = userMapper.toDto(findById(id));

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
  public UserDto updateByDto(UserDto userDto) {
    User user = findById(userDto.getId());
    userMapper.updateEntity(userDto, user);
    return userMapper.toDto(userRepository.save(user));
  }

  /**
   * Updates an existing user entity.
   *
   * @param user the updated user entity
   * @return the updated user entity
   */
  public User updateByEntity(User user) {
    return userRepository.save(user);
  }

  /**
   * Deletes user by ID.
   *
   * @param userId the ID of the user
   * @throws UserNotFoundException if the user does not exist
   */
  public void delete(long userId) {
    User user = findById(userId);
    userRepository.delete(user);
  }

  /**
   * Recalculate the interview counts for a list of users.
   *
   * @param users the list of users for whom the interview counts need to be refreshed. Each user's
   *              conducted and completed interview counts will be recalculated and updated in the
   *              database.
   */
  public void recalculateInterviewCounts(List<User> users) {
    users.forEach(user -> {
      long userId = user.getId();
      user.setConductedInterviews(interviewSummaryRepository.countByInterviewerId(userId));
      user.setCompletedInterviews(interviewSummaryRepository.countByCandidateId(userId));
      updateByEntity(user);
    });
  }

  /**
   * Retrieves all language proficiencies associated with the user.
   *
   * @param userId the ID of the user to associate the language proficiencies with
   * @return A list of LanguageProficiencyDto objects.
   */
  public List<LanguageProficiencyDto> findAllLanguageProficienciesByUserId(long userId) {
    User user = findById(userId);
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
    User user = findById(userId);
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
   * Retrieves EmploymentRecord (work experience) information by user ID.
   *
   * @param userId the ID of the user
   * @return the user's work experience as a DTO
   */
  public List<EmploymentRecordDto> getEmploymentRecordsByUserId(long userId) {
    User user = findById(userId);
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
    User user = findById(userId);
    EmploymentRecord employmentRecord = employmentRecordMapper.toEntity(employmentRecordDto);
    user.getEmploymentRecords().add(employmentRecord);
    updateByEntity(user);
    return employmentRecordMapper.toDto(employmentRecord);
  }

  /**
   * Retrieves the picture associated with a user by their user ID.
   *
   * @param userId the ID of the user whose picture is to be retrieved
   * @return the user's picture as a base64-encoded string
   */
  public UserPictureDto getPicture(long userId) {
    return new UserPictureDto(userRepository.findPictureByUserId(userId));
  }

  /**
   * Adds or updates a user's picture by user ID. If the user already has a picture, it is
   * replaced.
   *
   * @param userId      the ID of the user whose picture is to be added or updated
   * @param userPicture the picture data as a base64-encoded string
   */
  public void addPicture(long userId, String userPicture) {
    User user = findById(userId);
    user.setPicture(userPicture);
    updateByEntity(user);
  }

  /**
   * Removes a user's picture by user ID.
   *
   * @param userId the ID of the user whose picture is to be removed
   */
  public void deletePicture(long userId) {
    User user = findById(userId);
    user.setPicture(null);
    updateByEntity(user);
  }

  /**
   * Retrieves a list of achievements for a specific user by their ID.
   *
   * @param userId The ID of the user whose achievements are to be retrieved.
   * @return A list of AchievementDto objects representing the achievements of the user.
   */
  public List<AchievementDto> getAchievementsByUserId(long userId) {
    User user = findById(userId);
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
    User user = findById(userId);
    Achievement achievement = achievementMapper.toEntity(achievementDto);
    user.getAchievements().add(achievement);
    updateByEntity(user);
    return achievementMapper.toDto(achievement);
  }

  /**
   * Retrieves a list of education details for a specific user identified by their user ID.
   *
   * @param userId The unique identifier of the user.
   * @return A list of {@link EducationDto} objects representing the education details.
   */
  public List<EducationDto> getEducationsByUserId(long userId) {
    User user = findById(userId);
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
    User user = findById(userId);
    Education education = educationMapper.toEntity(educationDto);
    user.getEducations().add(education);
    updateByEntity(user);
    return educationMapper.toDto(education);
  }

  /**
   * Retrieves all contacts associated with the user.
   *
   * @param userId the ID of the user to associate the contacts with
   * @return A list of ContactDto objects.
   */
  public List<ContactDto> findAllContactsByUserId(long userId) {
    User user = findById(userId);
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
    User user = findById(userId);
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
    User user = findById(userId);
    return bookmarkMapper.toDto(user.getBookmarks());
  }

  /**
   * Creates a new bookmark for a specific user.
   *
   * @param userId      The ID of the user for whom the bookmark is to be created.
   * @param bookmarkDto The BookmarkDto object containing details of the bookmark to be created.
   */
  public void createBookmark(long userId, BookmarkDto bookmarkDto) {
    User user = findById(userId);
    Bookmark bookmark = bookmarkMapper.toEntity(bookmarkDto);
    user.getBookmarks().add(bookmark);
    updateByEntity(user);
  }

  /**
   * Retrieves all interview summaries associated with the user.
   *
   * @param userId the ID of the user to associate the interview summaries with
   * @return A list of InterviewSummaryDto objects.
   */
  public List<InterviewSummaryDto> getInterviewSummariesByUserId(long userId) {
    User user = findById(userId);
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
    User user = findById(userId);
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
    User user = findById(userId);
    return specializationDataMapper.toDto(user.getSpecializations());
  }

  /**
   * Retrieves a list of all main mastery skills for the specified user.
   *
   * @param userId the ID of the user whose mastery skills are to be retrieved.
   * @return a list of all main mastery skills for the user.
   */
  public List<UserMainMasterySkillDto> getPrivateMainMasterySkillsByUserId(long userId) {
    User user = findById(userId);
    return userMainMasterySkillMapper.toDto(user.getSpecializations());
  }

  /**
   * Retrieves a list of all main mastery skills for the specified user, excluding hidden skills.
   *
   * @param userId the ID of the user whose mastery skills are to be retrieved.
   * @return a list of main mastery skills for the user, excluding hidden skills.
   */
  public List<UserMainMasterySkillDto> getPublicMainMasterySkillsByUserId(long userId) {
    User user = findById(userId);
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
    User user = findById(userId);
    Specialization specialization = specializationDataMapper.toEntity(specializationDto);
    specialization.setUser(user);
    user.getSpecializations().add(specialization);
    updateByEntity(user);
    specializationService.createMasteriesForSpecialization(specialization,
        specializationDto.getMainMasteryLevel());
    return specializationDataMapper.toDto(specialization);
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
    User user = findById(userId);

    return user.getEvents().stream()
        .filter(event -> DateTimeUtils.isWithinRange(event.getStartTime().toLocalDate(), from, to))
        .map(event -> {
          User host = findById(event.getHostId());
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
    User user = findById(userId);

    return user.getEvents().stream()
        .filter(event -> !event.getStartTime().isBefore(DateTimeUtils.toUtc(from)))
        .sorted(Comparator.comparing(Event::getStartTime))
        .map(event -> {
          User host = findById(event.getHostId());
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
    InterviewFeedbackDetail feedbackDetail = interviewFeedbackDetailService.findById(
        interviewFeedbackDto.getInterviewFeedbackDetailId());

    if (!new HashSet<>(feedbackDetail.getSkillsIds())
        .equals(interviewFeedbackDto.getSkills().stream()
            .map(SkillFeedbackDto::getId)
            .collect(Collectors.toSet()))) {
      throw new ResourceNotFoundException("Input invalid skills");
    }

    InterviewRequestRole reviewerRole = interviewSummaryService.addComment(
        feedbackDetail.getInterviewSummaryId(), reviewerId, interviewFeedbackDto.getComment());

    skillService.updateMarks(interviewFeedbackDto.getSkills());

    masteryService.updateAverageMarks(
        feedbackDetail.getEvaluatedMasteryId(), reviewerRole);

    interviewFeedbackDetailService.delete(feedbackDetail.getId());
  }

  /**
   * Retrieves the main hard skills of a user.
   *
   * @param userId the ID of the user whose main hard skills are being retrieved
   * @return a list of {@link UserMainHardSkillsDto} representing the user's main hard skills
   * @throws UserNotFoundException if no user with the given ID is found
   */
  public List<UserMainHardSkillsDto> getMainHardSkills(long userId) {
    User user = findById(userId);
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
    User user = findById(userId);
    Notification notification = Notification.builder()
        .user(user)
        .read(notificationDto.isRead())
        .payload(notificationDto.getPayload())
        .type(notificationDto.getType())
        .createdAt(notificationDto.getCreatedAt())
        .build();
    notificationService.sendTestNotification(userEmail, notification);
  }
}
