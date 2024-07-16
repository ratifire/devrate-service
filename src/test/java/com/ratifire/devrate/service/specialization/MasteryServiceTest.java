package com.ratifire.devrate.service.specialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.MasteryRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the MasteryService class.
 */
@ExtendWith(MockitoExtension.class)
public class MasteryServiceTest {

  @InjectMocks
  private MasteryService masteryService;

  @Mock
  private MasteryRepository masteryRepository;

  @Mock
  private DataMapper dataMapper;

  private Mastery masteryJun;
  private Mastery masteryMid;
  private List<Mastery> masteryList;
  private MasteryDto masteryDtoJun;
  private Skill skill;
  private SkillDto skillDto;
  private List<SkillDto> skillDtos;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void setUp() {
    masteryJun = Mastery.builder()
        .id(1L)
        .level(1)
        .softSkillMark(BigDecimal.valueOf(5))
        .hardSkillMark(BigDecimal.valueOf(5))
        .skills(new ArrayList<>())
        .build();
    masteryMid = Mastery.builder()
        .id(2L)
        .level(2)
        .softSkillMark(BigDecimal.valueOf(6))
        .hardSkillMark(BigDecimal.valueOf(6))
        .skills(new ArrayList<>())
        .build();

    masteryDtoJun = MasteryDto.builder()
        .id(1L)
        .level("JUNIOR")
        .hardSkillMark(BigDecimal.valueOf(5))
        .softSkillMark(BigDecimal.valueOf(5))
        .build();

    masteryList = new ArrayList<>();
    masteryList.add(masteryJun);
    masteryList.add(masteryMid);

    skill = Skill.builder()
        .id(10L)
        .name("Test name")
        .averageMark(BigDecimal.valueOf(5))
        .build();

    skillDto = SkillDto.builder()
        .id(10L)
        .name("Test name")
        .averageMark(BigDecimal.valueOf(5))
        .build();

    skillDtos = new ArrayList<>();
    skillDtos.add(skillDto);
  }

  @Test
  public void updateTest() {
    when(dataMapper.toDto(masteryMid)).thenReturn(masteryDtoJun);
    when(masteryRepository.findById(anyLong())).thenReturn(Optional.of(masteryMid));

    MasteryDto result = masteryService.update(masteryDtoJun);

    assertNotNull(result);
    assertEquals(masteryDtoJun, result);
    verify(dataMapper).updateEntity(masteryDtoJun, masteryMid);
    verify(masteryRepository).save(masteryMid);
  }

  @Test
  public void getSoftSkillsByMasteryIdTest() {
    when(masteryRepository.findById(anyLong())).thenReturn(Optional.of(masteryMid));
    when(dataMapper.toDto(masteryMid.getSkills())).thenReturn(new ArrayList<>());

    List<SkillDto> result = masteryService.getSoftSkillsByMasteryId(masteryMid.getId());

    assertNotNull(result);
    assertEquals(masteryMid.getSkills().size(), result.size());
  }

  @Test
  public void getHardSkillsByMasteryIdTest() {
    when(masteryRepository.findById(anyLong())).thenReturn(Optional.of(masteryMid));
    when(dataMapper.toDto(masteryMid.getSkills())).thenReturn(new ArrayList<>());

    List<SkillDto> result = masteryService.getHardSkillsByMasteryId(masteryMid.getId());

    assertNotNull(result);
    assertEquals(masteryMid.getSkills().size(), result.size());
  }

  @Test
  public void createSkillTest() {
    when(masteryRepository.findById(anyLong())).thenReturn(Optional.of(masteryMid));
    when(dataMapper.toEntity(any(SkillDto.class))).thenReturn(skill);
    when(masteryRepository.existsByIdAndSkills_Name(anyLong(), anyString())).thenReturn(false);
    when(dataMapper.toDto(any(Skill.class))).thenReturn(skillDto);

    SkillDto result = masteryService.createSkill(skillDto, 1L);

    assertNotNull(result);
    assertEquals(skillDto, result);
    assertEquals(BigDecimal.valueOf(5), skillDto.getAverageMark());
    verify(masteryRepository).save(masteryMid);
  }

  @Test
  public void createSkillsTest() {
    List<Skill> skills = List.of(skill);

    when(masteryRepository.findById(anyLong())).thenReturn(Optional.of(masteryMid));
    when(dataMapper.toEntity(anyList())).thenReturn(skills);
    when(masteryRepository.existsByIdAndSkills_Name(anyLong(), anyString())).thenReturn(false);
    when(dataMapper.toDto(skills)).thenReturn(skillDtos);

    List<SkillDto> result = masteryService.createSkills(skillDtos, 1L);

    assertNotNull(result);
    assertEquals(skillDtos, result);
    assertEquals(BigDecimal.valueOf(5), skillDto.getAverageMark());
    verify(masteryRepository).save(masteryMid);
  }
}
