package com.ratifire.devrate.controller;


import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.dto.BookmarkDto;
import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.InterviewSummaryDto;
import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.dto.UserPictureDto;
import com.ratifire.devrate.service.user.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
  @PutMapping
  public UserDto update(@RequestBody UserDto userDto) {
    return userService.update(userDto);
  }

  /**
   * Deletes user personal information by user ID.
   *
   * @param id the ID of the user
   */
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
   * @param userId the ID of the user for whom the picture is to be added or updated
   * @param userPicture the picture data as a base64 string to upload
   */
  @PostMapping("/{userId}/pictures")
  public void addUserPicture(@PathVariable long userId, @RequestBody String userPicture) {
    userService.addUserPicture(userId, userPicture);
  }

  /**
   * Removes a user's picture.
   */
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
   * Deletes the association between a user and an interview summary.
   *
   * @param userId the ID of the user whose association with the interview summary is to be deleted
   * @param id the ID of the interview summary to be removed from the user's associations
   */
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
  @PostMapping("/{userId}/specializations")
  public SpecializationDto createSpecialization(
      @Valid @RequestBody SpecializationDto specializationDto, @PathVariable long userId) {
    return userService.createSpecialization(specializationDto, userId);
  }
}
