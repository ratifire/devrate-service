package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.service.user.UserService;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
   * Retrieves the picture associated with a user by their user ID.
   *
   * @param userId the ID of the user whose picture is to be retrieved
   * @return a ResponseEntity containing a map with the user's picture in byte array format if
   *     present; otherwise, returns no content status
   */
  @GetMapping("/{userId}/pictures")
  public ResponseEntity<Map<String, byte[]>> getUserPicture(@PathVariable long userId) {
    byte[] userPicture = userService.getUserPicture(userId);
    if (userPicture == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(Collections.singletonMap("picture", userPicture));
  }

  /**
   * Adds or updates a picture for a user by their user ID.
   *
   * @param userId the ID of the user for whom the picture is to be added or updated
   * @param userPicture the picture data as a byte array to upload
   * @return a Map containing the key 'picture' with the byte array of the uploaded picture as the
   *     value
   */
  @PostMapping("/{userId}/pictures")
  public Map<String, byte[]> addUserPicture(
      @PathVariable long userId, @RequestBody byte[] userPicture) {
    return Collections.singletonMap("picture", userService.addUserPicture(userId, userPicture));
  }

  /** Removes a user's picture. */
  @DeleteMapping("/{userId}/pictures")
  public void removeUserPicture(@PathVariable long userId) {
    userService.deleteUserPicture(userId);
  }
}
