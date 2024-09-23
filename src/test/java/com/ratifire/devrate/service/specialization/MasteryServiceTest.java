package com.ratifire.devrate.service.specialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.dto.MasteryHistoryDto;
import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.exception.ResourceAlreadyExistException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.MasteryHistoryRepository;
import com.ratifire.devrate.repository.MasteryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
  private MasteryHistoryDto historyDto;
  private LocalDate fromDate;
  private LocalDate toDate;

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

    historyDto = MasteryHistoryDto.builder()
        .masteryId(2L)
        .date(LocalDate.now())
        .softSkillMark(BigDecimal.valueOf(6))
        .hardSkillMark(BigDecimal.valueOf(6))
        .build();
    fromDate = LocalDate.now().minusDays(10);
    toDate = LocalDate.now();
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
  void createSkillTestSkillAlreadyExistsThrowResourceAlreadyExistException() {
    when(masteryRepository.existsByIdAndSkills_Name(anyLong(), anyString())).thenReturn(true);

    assertThrows(ResourceAlreadyExistException.class,
        () -> masteryService.createSkill(skillDto, 1L));
  }

  @Test
  public void createSkillsTest() {
    List<Skill> skills = List.of(skill);

    when(masteryRepository.findById(anyLong())).thenReturn(Optional.of(masteryMid));
    when(dataMapper.toEntity(skillDto)).thenReturn(skill);
    when(masteryRepository.existsByIdAndSkills_Name(anyLong(), anyString())).thenReturn(false);
    when(dataMapper.toDto(skills)).thenReturn(skillDtos);

    List<SkillDto> result = masteryService.createSkills(skillDtos, 1L);

    assertNotNull(result);
    assertEquals(skillDtos, result);
    assertEquals(BigDecimal.valueOf(5), skillDto.getAverageMark());
    verify(masteryRepository).save(masteryMid);
  }

  @Test
  void createSkillsTestSkillsEmptyReturnEmptySkillDtos() {
    when(masteryRepository.existsByIdAndSkills_Name(anyLong(), anyString())).thenReturn(true);

    List<SkillDto> result = masteryService.createSkills(skillDtos, 1L);

    assertTrue(result.isEmpty());
  }

  @Test
  public void testGetMasteryHistoryInRange() {
    when(masteryHistoryRepository.findByMasteryIdAndDateBetween(anyLong(),
        any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(new ArrayList<>());

    when(dataMapper.toDto(anyList())).thenReturn(Arrays.asList(historyDto));

    List<MasteryHistoryDto> result = masteryService.getMasteryHistory(2L, fromDate, toDate);

    assertNotNull(result);
    assertEquals(1, result.size());

    MasteryHistoryDto dto = result.get(0);
    assertEquals(historyDto.getMasteryId(), dto.getMasteryId());
    assertEquals(historyDto.getDate(), dto.getDate());
    assertEquals(historyDto.getSoftSkillMark(), dto.getSoftSkillMark());
    assertEquals(historyDto.getHardSkillMark(), dto.getHardSkillMark());
  }

  @Test
  public void testGetMasteryHistoryInRangeNotFound() {
    when(masteryHistoryRepository.findByMasteryIdAndDateBetween(anyLong(),
        any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(new ArrayList<>());

    when(dataMapper.toDto(anyList())).thenReturn(new ArrayList<>());

    List<MasteryHistoryDto> result = masteryService.getMasteryHistory(2L, fromDate, toDate);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}