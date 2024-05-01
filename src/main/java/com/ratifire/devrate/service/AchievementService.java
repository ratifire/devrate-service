package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.entity.Achievement;
import com.ratifire.devrate.exception.AchievementNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.AchievementRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing achievements.
 */
@Service
@RequiredArgsConstructor
public class AchievementService {

  private final AchievementRepository achievementRepository;
  private final DataMapper<AchievementDto, Achievement> mapper;

  /**
   * Retrieves all achievements for a given user.
   *
   * @param userId The ID of the user whose achievements are to be retrieved.
   * @return A list of AchievementDto objects representing the achievements.
   */
  public List<AchievementDto> getAllByUserId(long userId) {
    return achievementRepository.findAllByUserId(userId).stream().map(mapper::toDto).toList();
  }

  /**
   * Retrieves a specific achievement by its ID.
   *
   * @param id The ID of the achievement to retrieve.
   * @return The AchievementDto object representing the retrieved achievement.
   * @throws AchievementNotFoundException if the achievement with the given ID is not found.
   */
  public AchievementDto getById(long id) {
    return achievementRepository.findById(id).map(mapper::toDto)
        .orElseThrow(
            () -> new AchievementNotFoundException("Achievement not found with id: " + id));
  }

  /**
   * Creates a new achievement for a user.
   *
   * @param userId         The ID of the user for whom the achievement is created.
   * @param achievementDto The AchievementDto object containing information about the achievement to
   *                       be created.
   * @return The AchievementDto object representing the newly created achievement.
   */
  public AchievementDto create(long userId, AchievementDto achievementDto) {
    Achievement achievement = mapper.toEntity(achievementDto);
    achievement.setUserId(userId);

    return mapper.toDto(achievementRepository.save(achievement));
  }

  /**
   * Updates an existing achievement.
   *
   * @param id             The ID of the achievement to update.
   * @param achievementDto The AchievementDto object containing updated information about the
   *                       achievement.
   * @return The AchievementDto object representing the updated achievement.
   * @throws AchievementNotFoundException if the achievement with the given ID is not found.
   */
  public AchievementDto update(long id, AchievementDto achievementDto) {
    Achievement achievement = achievementRepository.findById(id)
        .orElseThrow(
            () -> new AchievementNotFoundException("Achievement not found with id: " + id));

    mapper.updateEntity(achievementDto, achievement);

    return mapper.toDto(achievementRepository.save(achievement));
  }

  /**
   * Deletes an achievement.
   *
   * @param id The ID of the achievement to delete.
   */
  public void delete(long id) {
    achievementRepository.deleteById(id);
  }
}
