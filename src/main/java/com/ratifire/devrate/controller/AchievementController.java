package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.service.AchievementService;
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
 * Controller class for managing achievements. This controller handles HTTP requests related to
 * achievements, such as retrieving, creating, updating, and deleting achievements.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/achievements")
public class AchievementController {

  private final AchievementService achievementService;

  /**
   * Retrieves all achievements for a given user.
   *
   * @param userId The ID of the user whose achievements are to be retrieved.
   * @return A list of AchievementDto objects representing the achievements.
   */
  @GetMapping("/all/{userId}")
  public List<AchievementDto> getAllByUserId(@PathVariable long userId) {
    return achievementService.getAllByUserId(userId);
  }

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
   * Creates a new achievement for a user.
   *
   * @param userId         The ID of the user for whom the achievement is created.
   * @param achievementDto The AchievementDto object containing information about the achievement to
   *                       be created.
   * @return The AchievementDto object representing the newly created achievement.
   */
  @PostMapping("/{userId}")
  public AchievementDto create(@PathVariable long userId,
      @RequestBody @Valid AchievementDto achievementDto) {
    return achievementService.create(userId, achievementDto);
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
