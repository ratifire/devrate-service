package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.entity.Achievement;
import com.ratifire.devrate.exception.AchievementNotFountException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.AchievementRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AchievementService {

  private final AchievementRepository achievementRepository;
  private final DataMapper<AchievementDto, Achievement> mapper;

  public List<AchievementDto> getAllByUserId(long userId) {
    return achievementRepository.findAllByUserId(userId).stream().map(mapper::toDto).toList();
  }

  public AchievementDto getById(long id) {
    return achievementRepository.findById(id).map(mapper::toDto)
        .orElseThrow(
            () -> new AchievementNotFountException("Achievement not found with id: " + id));
  }

  public AchievementDto create(long userId, AchievementDto achievementDto) {
    Achievement achievement = mapper.toEntity(achievementDto);
    achievement.setUserId(userId);

    return mapper.toDto(achievementRepository.save(achievement));
  }

  public AchievementDto update(long id, AchievementDto achievementDto) {
    Achievement achievement = achievementRepository.findById(id)
        .orElseThrow(
            () -> new AchievementNotFountException("Achievement not found with id: " + id));

    mapper.updateEntity(achievementDto, achievement);

    return mapper.toDto(achievementRepository.save(achievement));
  }

  public void delete(long id) {
    achievementRepository.deleteById(id);
  }
}
