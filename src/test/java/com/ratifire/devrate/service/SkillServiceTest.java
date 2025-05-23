package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.SkillRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the SkillService class.
 */
@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

  @InjectMocks
  private SkillService skillService;

  @Mock
  private SkillRepository skillRepository;

  @Mock
  private DataMapper dataMapper;

  private Skill skill;
  private SkillDto skillDto;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void setUp() {
    skill = Skill.builder()
        .id(10L)
        .name("Test name")
        .averageMark(BigDecimal.valueOf(5))
        .counter(1)
        .hidden(true)
        .build();

    skillDto = SkillDto.builder()
        .id(10L)
        .name("Test name")
        .averageMark(BigDecimal.valueOf(5))
        .hidden(true)
        .build();
  }

  @Test
  public void updateUpdateHiddenStatusTest() {
    when(skillRepository.findById(anyLong())).thenReturn(Optional.of(skill));
    when(dataMapper.toDto(any(Skill.class))).thenReturn(skillDto);
    when(skillRepository.save(any(Skill.class))).thenReturn(skill);

    SkillDto result = skillService.updateHiddenStatus(1L, true);
    assertEquals(skillDto, result);
  }
}
