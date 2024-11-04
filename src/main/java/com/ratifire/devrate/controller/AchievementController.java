package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.service.AchievementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing achievements.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/achievements")
public class AchievementController {

  private final AchievementService achievementService;

  /**
   * Retrieves a specific achievement by its ID.
   *
   * @param id The ID of the achievement to retrieve.
   * @return The AchievementDto object representing the retrieved achievement.
   */
  @GetMapping("/{id}")
  public AchievementDto getById(@PathVariable long id) {
    return achievementService.getById(id);
  }

  /**
   * Updates an existing achievement.
   *
   * @param id             The ID of the achievement to update.
   * @param achievementDto The AchievementDto object containing updated information about the
   *                       achievement.
   * @return The AchievementDto object representing the updated achievement.
   */
  @PutMapping("/{id}")
  public AchievementDto update(@PathVariable long id,
      @RequestBody @Valid AchievementDto achievementDto) {
    return achievementService.update(id, achievementDto);
  }

  /**
   * Deletes an achievement.
   *
   * @param id The ID of the achievement to delete.
   */
  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    achievementService.delete(id);
  }
}
