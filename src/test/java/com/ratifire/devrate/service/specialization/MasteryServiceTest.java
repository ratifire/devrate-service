package com.ratifire.devrate.service.specialization;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.MasteryHistory;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.MasteryHistoryRepository;
import com.ratifire.devrate.repository.MasteryRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
  private MasteryHistoryRepository masteryHistoryRepository;

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
    Mockito.when(dataMapper.toDto(masteryMid)).thenReturn(masteryDtoJun);
    Mockito.when(masteryRepository.findById(anyLong())).thenReturn(Optional.of(masteryMid));

    MasteryDto result = masteryService.update(masteryDtoJun);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(masteryDtoJun, result);
    Mockito.verify(dataMapper).updateEntity(masteryDtoJun, masteryMid);
    Mockito.verify(masteryRepository).save(masteryMid);
  }

  @Test
  public void getSoftSkillsByMasteryIdTest() {
    Mockito.when(masteryRepository.findById(anyLong())).thenReturn(Optional.of(masteryMid));
    Mockito.when(dataMapper.toDto(masteryMid.getSkills())).thenReturn(new ArrayList<>());

    List<SkillDto> result = masteryService.getSoftSkillsByMasteryId(masteryMid.getId());

    Assertions.assertNotNull(result);
    Assertions.assertEquals(masteryMid.getSkills().size(), result.size());
  }

  @Test
  public void getHardSkillsByMasteryIdTest() {
    Mockito.when(masteryRepository.findById(anyLong())).thenReturn(Optional.of(masteryMid));
    Mockito.when(dataMapper.toDto(masteryMid.getSkills())).thenReturn(new ArrayList<>());

    List<SkillDto> result = masteryService.getHardSkillsByMasteryId(masteryMid.getId());

    Assertions.assertNotNull(result);
    Assertions.assertEquals(masteryMid.getSkills().size(), result.size());
  }

  @Test
  public void createSkillTest() {
    Mockito.when(masteryRepository.findById(anyLong())).thenReturn(Optional.of(masteryMid));
    Mockito.when(dataMapper.toEntity(any(SkillDto.class))).thenReturn(skill);
    Mockito.when(masteryRepository.existsByIdAndSkills_Name(anyLong(), anyString()))
        .thenReturn(false);
    Mockito.when(dataMapper.toDto(any(Skill.class))).thenReturn(skillDto);

    SkillDto result = masteryService.createSkill(skillDto, 1L);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(skillDto, result);
    Assertions.assertEquals(BigDecimal.valueOf(5), skillDto.getAverageMark());
    Mockito.verify(masteryRepository).save(masteryMid);
  }

  @Test
  public void createSkillsTest() {
    List<Skill> skills = List.of(skill);

    Mockito.when(masteryRepository.findById(anyLong())).thenReturn(Optional.of(masteryMid));
    Mockito.when(dataMapper.toEntity(anyList())).thenReturn(skills);
    Mockito.when(masteryRepository.existsByIdAndSkills_Name(anyLong(), anyString()))
        .thenReturn(false);
    Mockito.when(dataMapper.toDto(skills)).thenReturn(skillDtos);

    List<SkillDto> result = masteryService.createSkills(skillDtos, 1L);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(skillDtos, result);
    Assertions.assertEquals(BigDecimal.valueOf(5), skillDto.getAverageMark());
    Mockito.verify(masteryRepository).save(masteryMid);
  }

  @Test
  public void testSaveHistory() {
    masteryService.saveHistory(masteryMid);

    ArgumentCaptor<MasteryHistory> historyCaptor = ArgumentCaptor.forClass(MasteryHistory.class);
    Mockito.verify(masteryHistoryRepository, Mockito.times(1)).save(historyCaptor.capture());

    MasteryHistory capturedHistory = historyCaptor.getValue();
    Assertions.assertEquals(masteryMid, capturedHistory.getMastery());
    Assertions.assertEquals(masteryMid.getSoftSkillMark(), capturedHistory.getSoftSkillMark());
    Assertions.assertEquals(masteryMid.getHardSkillMark(), capturedHistory.getHardSkillMark());
  }

  @Test
  public void testGetMasteryHistory() {
    List<MasteryHistory> historyList = new ArrayList<>();
    MasteryHistory history = MasteryHistory.builder()
        .id(1L)
        .mastery(masteryMid)
        .timestamp(new Date())
        .softSkillMark(BigDecimal.valueOf(6))
        .hardSkillMark(BigDecimal.valueOf(6))
        .build();
    historyList.add(history);

    Mockito.when(masteryHistoryRepository.findHistoriesByMasteryId(2L)).thenReturn(historyList);

    List<MasteryHistory> result = masteryService.getMasteryHistory(2L);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(history, result.get(0));
  }

  @Test
  public void testGetMasteryHistoryNotFound() {
    Mockito.when(masteryHistoryRepository.findHistoriesByMasteryId(2L))
        .thenReturn(new ArrayList<>());

    Assertions.assertThrows(ResourceNotFoundException.class, () -> masteryService
        .getMasteryHistory(2L));
  }

}
