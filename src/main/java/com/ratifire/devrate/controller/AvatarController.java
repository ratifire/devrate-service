package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.AvatarDto;
import com.ratifire.devrate.service.avatar.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for handling avatar-related requests. Maps operations for a user's avatar, such as
 * retrieval, addition, update, and deletion to RESTful endpoints.
 */
@RestController
@RequestMapping("/avatar")
public class AvatarController {
  private final AvatarService avatarService;

  /**
   * Constructs the controller with required services.
   *
   * @param avatarService Service for avatar operations.
   */
  @Autowired
  public AvatarController(AvatarService avatarService) {
    this.avatarService = avatarService;
  }

  /**
   * Retrieves the avatar path for a given user.
   *
   * @param userId The ID of the user.
   * @return Path to the user's avatar.
   */
  @GetMapping("/{userId}")
  public ResponseEntity<AvatarDto> getAvatarPath(@PathVariable long userId) {
    AvatarDto avatarDto = avatarService.get(userId);
    return ResponseEntity.ok(avatarDto);
  }

  /**
   * Adds a new avatar for a user.
   *
   * @param userId The ID of the user.
   * @param avatarFile The avatar file to upload.
   * @return URL of the uploaded avatar.
   */
  @PostMapping("/{userId}")
  public ResponseEntity<AvatarDto> addAvatar(
      @PathVariable long userId, @RequestParam("avatar") MultipartFile avatarFile) {
    AvatarDto avatarDto = avatarService.add(userId, avatarFile);
    return ResponseEntity.status(HttpStatus.CREATED).body(avatarDto);
  }

  /**
   * Updates an existing avatar for a user.
   *
   * @param userId The ID of the user.
   * @param avatarFile New avatar file to replace the existing one.
   * @return URL of the updated avatar.
   */
  @PutMapping("/{userId}")
  public ResponseEntity<AvatarDto> updateAvatar(
      @PathVariable long userId, @RequestParam("avatar") MultipartFile avatarFile) {
    AvatarDto avatarDto = avatarService.update(userId, avatarFile);
    return ResponseEntity.ok(avatarDto);
  }

  /**
   * Removes a user's avatar.
   *
   * @param userId The ID of the user whose avatar is to be removed.
   * @return A response entity indicating the operation's success.
   */
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> removeAvatar(@PathVariable long userId) {
    avatarService.remove(userId);
    return ResponseEntity.ok().build();
  }
}
