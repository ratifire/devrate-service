package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.entity.Achievement;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.AchievementRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the AchievementService class.
 */
@ExtendWith(MockitoExtension.class)
public class AchievementServiceTest {

  @Mock
  private AchievementRepository achievementRepository;

  @Mock
  private DataMapper<AchievementDto, Achievement> mapper;

  @InjectMocks
  private AchievementService achievementService;

  private AchievementDto achievementDto;

  private Achievement achievement;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void before() {
    achievement = Achievement.builder()
        .id(1).link("https://certificate.ithillel.ua/view/86277355")
        .summary("summary")
        .description("description")
        .build();

    achievementDto = AchievementDto.builder()
        .id(1).link("https://certificate.ithillel.ua/view/86277355")
        .summary("summary")
        .description("description")
        .build();
  }

  @Test
  public void updateTest() {
    when(achievementRepository.findById(anyLong())).thenReturn(Optional.ofNullable(achievement));
    when(achievementRepository.save(any())).thenReturn(achievement);
    when(mapper.updateEntity(any(), any())).thenReturn(achievement);
    when(mapper.toDto(achievement)).thenReturn(achievementDto);

    AchievementDto result = achievementService.update(anyLong(), achievementDto);

    assertEquals(achievementDto, result);
  }
}
