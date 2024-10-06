package com.ratifire.devrate.controller;

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
import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.dto.UserMainHardSkillsDto;
import com.ratifire.devrate.dto.UserMainMasterySkillDto;
import com.ratifire.devrate.dto.UserPictureDto;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.service.user.UserService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling requests related to user personal information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  /**
   * Retrieves user personal information by user ID.
   *
   * @param id the ID of the user
   * @return the user's personal information as a DTO
   */
  @GetMapping("/{id}")
  public UserDto findById(@PathVariable long id) {
    return userService.findById(id);
  }

  /**
   * Updates user personal information by user ID.
   *
   * @param userDto the updated user's personal information as a DTO
   * @return the updated user personal information as a DTO
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userDto.id)")
  @PutMapping
  public UserDto update(@Valid @RequestBody UserDto userDto) {
    return userService.update(userDto);
  }

  /**
   * Deletes user personal information by user ID.
   *
   * @param id the ID of the user
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#id)")
  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    userService.delete(id);
  }

  /**
   * Retrieves all contacts associated with a user.
   *
   * @param userId the ID of the user to associate the contacts with
   * @return A list of ContactDto objects.
   */
  @GetMapping("/{userId}/contacts")
  public List<ContactDto> findAllContactsByUserId(@PathVariable long userId) {
    return userService.findAllContactsByUserId(userId);
  }

  /**
   * Saves contacts for a user.
   *
   * @param userId      the ID of the user to associate the contacts with
   * @param contactDtos the contact information to save
   * @return the list of saved ContactDto objects
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId)")
  @PostMapping("/{userId}/contacts")
  public List<ContactDto> saveContacts(@PathVariable long userId,
      @Valid @RequestBody List<ContactDto> contactDtos) {
    return userService.saveContacts(userId, contactDtos);
  }

  /**
   * Retrieves user employment-record information by user ID.
   *
   * @param userId the ID of the user
   * @return the list of user's employment-records information as a DTO
   */
  @GetMapping("/{userId}/employment-records")
  public List<EmploymentRecordDto> getEmploymentRecordsByUserId(@PathVariable long userId) {
    return userService.getEmploymentRecordsByUserId(userId);
  }

  /**
   * Creates user employment-record information by user ID.
   *
   * @param employmentRecordDto the user's employment-record information as a DTO
   * @return the created user employment-record information as a DTO
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId)")
  @PostMapping("/{userId}/employment-records")
  public EmploymentRecordDto createEmploymentRecord(
      @Valid @RequestBody EmploymentRecordDto employmentRecordDto,
      @PathVariable long userId) {
    return userService.createEmploymentRecord(employmentRecordDto, userId);
  }

  /**
   * Retrieves all language proficiencies associated with a user.
   *
   * @param userId the ID of the user to associate the language proficiencies with
   * @return A list of LanguageProficiencyDto objects.
   */
  @GetMapping("/{userId}/language-proficiencies")
  public List<LanguageProficiencyDto> findAllLanguageProficienciesByUserId(
      @PathVariable long userId) {
    return userService.findAllLanguageProficienciesByUserId(userId);
  }

  /**
   * Saves new language proficiencies for a user.
   *
   * @param userId                  the ID of the user to associate the new language proficiencies
   *                                with
   * @param languageProficiencyDtos the language proficiency information to save
   * @return the list of saved LanguageProficiencyDto objects
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId)")
  @PostMapping("/{userId}/language-proficiencies")
  public List<LanguageProficiencyDto> saveLanguageProficiencies(@PathVariable long userId,
      @Valid @RequestBody List<LanguageProficiencyDto> languageProficiencyDtos) {
    return userService.saveLanguageProficiencies(userId, languageProficiencyDtos);
  }

  /**
   * Retrieves the picture associated with a user by their user ID.
   *
   * @param userId the ID of the user whose picture is to be retrieved
   * @return a ResponseEntity containing a UserPictureDto with the user's picture
   *     as a base64-encoded string if present; otherwise, returns no content status
   */
  @GetMapping("/{userId}/pictures")
  public ResponseEntity<UserPictureDto> getUserPicture(@PathVariable long userId) {
    UserPictureDto userPicture = userService.getUserPicture(userId);
    return userPicture.getUserPicture() != null
        ? ResponseEntity.ok(userPicture)
        : ResponseEntity.noContent().build();
  }

  /**
   * Adds or updates a picture for a user by their user ID.
   *
   * @param userId      the ID of the user for whom the picture is to be added or updated
   * @param userPicture the picture data as a base64 string to upload
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId)")
  @PostMapping("/{userId}/pictures")
  public void addUserPicture(@PathVariable long userId, @RequestBody String userPicture) {
    userService.addUserPicture(userId, userPicture);
  }

  /**
   * Removes a user's picture.
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId)")
  @DeleteMapping("/{userId}/pictures")
  public void removeUserPicture(@PathVariable long userId) {
    userService.deleteUserPicture(userId);
  }

  /**
   * Retrieves a list of achievements for a specific user by their ID.
   *
   * @param userId The ID of the user whose achievements are to be retrieved.
   * @return A list of AchievementDto objects representing the achievements of the user.
   */
  @GetMapping("/{userId}/achievements")
  public List<AchievementDto> getAchievementsByUserId(@PathVariable long userId) {
    return userService.getAchievementsByUserId(userId);
  }

  /**
   * Creates a new achievement for a specific user.
   *
   * @param userId         The ID of the user for whom the achievement is to be created.
   * @param achievementDto The AchievementDto object containing details of the achievement to be
   *                       created.
   * @return The AchievementDto object representing the created achievement.
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId)")
  @PostMapping("/{userId}/achievements")
  public AchievementDto createAchievement(@PathVariable long userId,
      @RequestBody @Valid AchievementDto achievementDto) {
    return userService.createAchievement(userId, achievementDto);
  }

  /**
   * Retrieves a list of education details for a specific user identified by their user ID.
   *
   * @param userId The unique identifier of the user.
   * @return A list of {@link EducationDto} objects representing the education details.
   */
  @GetMapping("/{userId}/educations")
  public List<EducationDto> getEducationsByUserId(@PathVariable long userId) {
    return userService.getEducationsByUserId(userId);
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
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId)")
  @PostMapping("/{userId}/educations")
  public EducationDto createEducation(@PathVariable long userId,
      @RequestBody @Valid EducationDto educationDto) {
    return userService.createEducation(userId, educationDto);
  }

  /**
   * Retrieves a list of bookmarks for the specified user by their ID.
   *
   * @param userId The ID of the user whose bookmarks are to be retrieved.
   * @return A list of BookmarkDto objects representing the bookmarks of the user.
   */
  @GetMapping("/{userId}/bookmarks")
  public List<BookmarkDto> getBookmarksByUserId(@PathVariable long userId) {
    return userService.getBookmarksByUserId(userId);
  }

  /**
   * Creates a new bookmark for the specified user.
   *
   * @param userId      The ID of the user for whom the bookmark is to be created.
   * @param bookmarkDto The BookmarkDto object containing details of the bookmark to be created.
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId)")
  @PostMapping("/{userId}/bookmarks")
  public void createBookmark(@PathVariable long userId,
      @RequestBody @Valid BookmarkDto bookmarkDto) {
    userService.createBookmark(userId, bookmarkDto);
  }

  /**
   * Retrieves a list of user`s interview summaries information by user ID.
   *
   * @param userId the ID of the user
   * @return the list of user's InterviewSummary information as a DTO
   */
  @GetMapping("/{userId}/interview-summaries")
  public List<InterviewSummaryDto> getInterviewSummariesByUserId(@PathVariable long userId) {
    return userService.getInterviewSummariesByUserId(userId);
  }

  /**
   * Retrieves a list of conducted and passed interviews by user ID and date range.
   *
   * @param userId the ID of the user
   * @param from   the start date of the date range (inclusive)
   * @param to     the end date of the date range (inclusive)
   * @return the list of conducted and passed interviews as a DTO
   */
  @GetMapping("/{userId}/interview-summaries/statistics")
  public List<InterviewStatsConductedPassedByDateDto> getInterviewsConductedPassed(
      @PathVariable long userId,
      @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    return userService.getInterviewStatConductedPassedByDate(userId, from, to);
  }

  /**
   * Deletes the association between a user and an interview summary.
   *
   * @param userId the ID of the user whose association with the interview summary is to be deleted
   * @param id     the ID of the interview summary to be removed from the user's associations
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId) && "
      + "@resourceAuthorizationService.isResourceOwnedByLoggedUser('interview-summaries' , #id)")
  @DeleteMapping("/{userId}/interview-summaries/{id}")
  public void deleteInterviewSummary(@PathVariable long userId, @PathVariable long id) {
    userService.deleteInterviewSummary(userId, id);
  }

  /**
   * Retrieves user`s Specialization information by user ID.
   *
   * @param userId the ID of the user
   * @return the list of user's Specialization information as a DTO
   */
  @GetMapping("/{userId}/specializations")
  public List<SpecializationDto> getSpecializationsByUserId(@PathVariable long userId) {
    return userService.getSpecializationsByUserId(userId);
  }

  /**
   * Creates user`s Specialization information by user ID.
   *
   * @param specializationDto the user's Specialization information as a DTO
   * @return the created user Specialization information as a DTO
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId)")
  @PostMapping("/{userId}/specializations")
  public SpecializationDto createSpecialization(
      @Valid @RequestBody SpecializationDto specializationDto, @PathVariable long userId) {
    return userService.createSpecialization(specializationDto, userId);
  }

  /**
   * Retrieves all main mastery skills for the specified user.
   *
   * @param userId the ID of the user whose private mastery skills are being retrieved.
   * @return a list of all main mastery skills for the user, including hidden skills.
   */
  @GetMapping("/{userId}/private-skills")
  public List<UserMainMasterySkillDto> getPrivateMainMasterySkillsByUserId(
      @PathVariable long userId) {
    return userService.getPrivateMainMasterySkillsByUserId(userId);
  }

  /**
   * Retrieves all main mastery skills for the specified user, excluding hidden skills.
   *
   * @param userId the ID of the user whose public mastery skills are being retrieved.
   * @return a list of all main mastery skills for the user, excluding hidden skills.
   */
  @GetMapping("/{userId}/public-skills")
  public List<UserMainMasterySkillDto> getPublicMainMasterySkillsByUserId(
      @PathVariable long userId) {
    return userService.getPublicMainMasterySkillsByUserId(userId);
  }

  /**
   * Adds an interview request for the specified user.
   *
   * @param userId           the ID of the user
   * @param interviewRequest the interview request data
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId)")
  @PostMapping("/{userId}/interview-requests")
  public void createInterviewRequest(@PathVariable long userId,
      @Valid @RequestBody InterviewRequestDto interviewRequest) {
    userService.createAndMatchInterviewRequest(userId, interviewRequest);
  }

  /**
   * Retrieves the interview request for a specific user and role.
   *
   * @param userId the ID of the user
   * @param role   the role for which the interview request is being retrieved
   * @return the InterviewRequestDto containing the interview request details
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId)")
  @GetMapping("/{userId}/interview-requests")
  public InterviewRequestDto getInterviewRequest(@PathVariable long userId,
      @Valid @RequestParam InterviewRequestRole role) {
    return userService.getInterviewRequest(userId, role);
  }

  /**
   * Updates the interview request for a specific user.
   *
   * @param userId           the ID of the user
   * @param interviewRequest the InterviewRequestDto containing the updated interview request
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId)")
  @PutMapping("/{userId}/interview-requests")
  public void updateInterviewRequest(@PathVariable long userId,
      @Valid @RequestBody InterviewRequestDto interviewRequest) {
    userService.updateAndMatchInterviewRequest(userId, interviewRequest);
  }

  /**
   * Delete an interview for the specified user.
   *
   * @param userId the ID of the user who rejected the interview
   * @param id     the rejected interview ID
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId) && "
      + "@resourceAuthorizationService.isResourceOwnedByLoggedUser('interviews' , #id)")
  @DeleteMapping("/{userId}/interviews/{id}")
  public void deleteRejectedInterview(@PathVariable long userId, @PathVariable long id) {
    userService.deleteRejectedInterview(userId, id);
  }

  /**
   * Retrieves a list of events for a specified user that occur within a given date range.
   *
   * @param userId the ID of the user whose events are to be retrieved
   * @param from   the start of the date range (inclusive)
   * @param to     the end of the date range (inclusive)
   * @return a list of {@link EventDto} objects representing the events for the user
   */
  @GetMapping("/{userId}/events")
  public List<EventDto> findEventsBetweenDate(@PathVariable long userId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    return userService.findEventsBetweenDate(userId, from, to);
  }

  /**
   * Retrieves a list of events for a given user that start from a specified date and time.
   *
   * @param userId the ID of the user whose events are to be retrieved
   * @param from   the starting date and time in ISO format (e.g., {@code 2024-08-28T12:00:00Z})
   * @return a list of {@link EventDto} objects representing the events starting from
   */
  @GetMapping("/{userId}/events/closest")
  public List<EventDto> findEventsFromDateTime(@PathVariable long userId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from) {
    return userService.findEventsFromDateTime(userId, from);
  }

  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#reviewerId) && "
      + "@resourceAuthorizationService.isResourceOwnedByLoggedUser('interviewFeedbackDetails' ,"
      + " #interviewFeedbackDto.interviewFeedbackDetailId)")
  @PostMapping("/{reviewerId}/feedbacks")
  public void saveFeedback(@PathVariable long reviewerId,
      @Valid @RequestBody InterviewFeedbackDto interviewFeedbackDto) {
    userService.saveFeedback(reviewerId, interviewFeedbackDto);
  }

  @GetMapping("/{userId}/specializations/main-mastery/skills")
  public List<UserMainHardSkillsDto> getMainHardSkills(@PathVariable long userId) {
    return userService.getMainHardSkills(userId);
  }
}
