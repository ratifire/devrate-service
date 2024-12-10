package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.entity.Achievement;
import com.ratifire.devrate.exception.AchievementNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing achievements.
 */
@Service
@RequiredArgsConstructor
public class AchievementService {

  private final AchievementRepository repository;
  private final DataMapper<AchievementDto, Achievement> mapper;

  /**
   * Retrieves a specific achievement by its ID.
   *
   * @param id The ID of the achievement to retrieve.
   * @return The AchievementDto object representing the retrieved achievement.
   * @throws AchievementNotFoundException if the achievement with the given ID isn't found.
   */
  public AchievementDto getById(long id) {
    return repository.findById(id).map(mapper::toDto)
        .orElseThrow(() -> new AchievementNotFoundException(id));
  }

  /**
   * Updates an existing achievement.
   *
   * @param id             The ID of the achievement to update.
   * @param achievementDto The AchievementDto object containing updated information about the
   *                       achievement.
   * @return The AchievementDto object representing the updated achievement.
   * @throws AchievementNotFoundException if the achievement with the given ID isn't found.
   */
  public AchievementDto update(long id, AchievementDto achievementDto) {
    Achievement achievement = repository.findById(id)
        .orElseThrow(() -> new AchievementNotFoundException(id));

    mapper.updateEntity(achievementDto, achievement);

    return mapper.toDto(repository.save(achievement));
  }

  /**
   * Deletes an achievement.
   *
   * @param id The ID of the achievement to delete.
   */
  public void delete(long id) {
    repository.deleteById(id);
  }
}
