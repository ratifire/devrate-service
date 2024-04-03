package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling requests related to user personal information.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
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
}
