package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.UserInfoDto;
import com.ratifire.devrate.service.userinfo.UserInfoService;
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
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserInfoController {

  private final UserInfoService userInfoService;

  /**
   * Retrieves user personal information by user ID.
   *
   * @param userId the ID of the user
   * @return the user's personal information as a DTO
   */
  @GetMapping("/{userId}")
  public UserInfoDto findByUserId(@PathVariable long userId) {
    return userInfoService.findByUserId(userId);
  }

  /**
   * Creates user personal information by user ID.
   *
   * @param userInfoDto the user's personal information as a DTO
   * @return the created user personal information as a DTO
   */
  @PostMapping()
  public UserInfoDto create(@RequestBody UserInfoDto userInfoDto) {
    return userInfoService.create(userInfoDto);
  }

  /**
   * Updates user personal information by user ID.
   *
   * @param userInfoDto the updated user's personal information as a DTO
   * @return the updated user personal information as a DTO
   */
  @PutMapping()
  public UserInfoDto update(@RequestBody UserInfoDto userInfoDto) {
    return userInfoService.update(userInfoDto);
  }

  /**
   * Deletes user personal information by user ID.
   *
   * @param userId the ID of the user whose personal information is to be deleted
   */
  @DeleteMapping("/{userId}")
  public void delete(@PathVariable long userId) {
    userInfoService.delete(userId);
  }
}