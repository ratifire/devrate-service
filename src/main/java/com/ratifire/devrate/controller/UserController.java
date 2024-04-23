package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.service.employmentrecord.EmploymentRecordService;
import com.ratifire.devrate.service.user.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
   * Creates a new language proficiency for a user.
   *
   * @param userId                 the ID of the user to associate the new language proficiency
   *                               with
   * @param languageProficiencyDto the language proficiency data to create
   * @return LanguageProficiencyDto the created language proficiency data
   */
  @PostMapping("/{userId}/language-proficiencies")
  public LanguageProficiencyDto createLanguageProficiency(@PathVariable long userId,
      @Valid @RequestBody LanguageProficiencyDto languageProficiencyDto) {
    return userService.createLanguageProficiency(userId, languageProficiencyDto);
  }

}
