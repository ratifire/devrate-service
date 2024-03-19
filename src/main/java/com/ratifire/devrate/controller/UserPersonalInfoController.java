package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.UserPersonalInfoDto;
import com.ratifire.devrate.service.userinfo.UserPersonalInfoService;
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
@RequestMapping("/personal-info")
@RequiredArgsConstructor
public class UserPersonalInfoController {

  private final UserPersonalInfoService userPersonalInfoService;

  /**
   * Retrieves user personal information by user ID.
   *
   * @param userId the ID of the user
   * @return the user's personal information as a DTO
   */
  @GetMapping("/{userId}")
  public UserPersonalInfoDto getByUserId(@PathVariable long userId) {
    return userPersonalInfoService.getById(userId);
  }

  /**
   * Creates user personal information by user ID.
   *
   * @param userId              the ID of the user
   * @param userPersonalInfoDto the user's personal information as a DTO
   * @return the created user personal information as a DTO
   */
  @PostMapping("/{userId}")
  public UserPersonalInfoDto create(@PathVariable long userId,
      @RequestBody UserPersonalInfoDto userPersonalInfoDto) {
    return userPersonalInfoService.create(userId, userPersonalInfoDto);
  }

  /**
   * Updates user personal information by user ID.
   *
   * @param userId              the ID of the user
   * @param userPersonalInfoDto the updated user's personal information as a DTO
   * @return the updated user personal information as a DTO
   */
  @PutMapping("/{userId}")
  public UserPersonalInfoDto update(@PathVariable long userId,
      @RequestBody UserPersonalInfoDto userPersonalInfoDto) {
    return userPersonalInfoService.update(userId, userPersonalInfoDto);
  }

  /**
   * Deletes user personal information by user ID.
   *
   * @param userId the ID of the user whose personal information is to be deleted
   */
  @DeleteMapping("/{userId}")
  public void delete(@PathVariable long userId) {
    userPersonalInfoService.delete(userId);
  }
}
