package com.ratifire.devrate.service.specialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.enums.MasteryLevel;
import com.ratifire.devrate.exception.ResourceAlreadyExistException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.SpecializationRepository;
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
 * Unit tests for the SpecializationService class.
 */
@ExtendWith(MockitoExtension.class)
public class SpecializationServiceTest {

  @InjectMocks
  private SpecializationService specializationService;

  @Mock
  private SpecializationRepository specializationRepository;
  @Mock
  private DataMapper dataMapper;
  @Mock
  private MasteryService masteryService;

  private SpecializationDto specializationDto;
  private Specialization specialization;
  private Mastery junMastery;
  private Mastery midMastery;
  private MasteryDto masteryDto;
  private final long specId = 6661L;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void setUp() {
    midMastery = Mastery.builder()
        .id(1L)
        .name(MasteryLevel.MIDDLE)
        .hardSkillMark(BigDecimal.valueOf(1))
        .softSkillMark(BigDecimal.valueOf(2)).build();

    junMastery = Mastery.builder()
        .id(2L)
        .name(MasteryLevel.JUNIOR)
        .hardSkillMark(BigDecimal.valueOf(2))
        .softSkillMark(BigDecimal.valueOf(3)).build();

    masteryDto = MasteryDto.builder()
        .id(1L)
        .name(MasteryLevel.MIDDLE)
        .hardSkillMark(BigDecimal.valueOf(1))
        .softSkillMark(BigDecimal.valueOf(2))
        .build();

    specializationDto = SpecializationDto.builder()
        .id(specId)
        .name("Frontend Developer")
        .main(true)
        .build();

    specialization = Specialization.builder()
        .id(specId)
        .main(true)
        .name("Frontend Developer")
        .completedInterviews(11)
        .conductedInterviews(4)
        .mainMastery(junMastery)
        .user(User.builder().build())
        .build();
  }

  @Test
  void getMainMasteryById_shouldReturnMainMastery_whenMainMasteryExists() {
    Mastery mainMastery = new Mastery();
    masteryDto = MasteryDto.builder().build();
    specialization.setMainMastery(mainMastery);

    when(specializationRepository.findById(any())).thenReturn(Optional.of(specialization));
    when(dataMapper.toDto(mainMastery)).thenReturn(masteryDto);

    MasteryDto result = specializationService.getMainMasteryById(specId);

    assertNotNull(result);
    assertEquals(masteryDto, result);
  }

  @Test
  void getMainMasteryById_shouldThrowResourceNotFoundException_whenMainMasteryDoesNotExist() {
    when(specializationRepository.findById(anyLong())).thenReturn(
        Optional.of(new Specialization()));

    assertThrows(ResourceNotFoundException.class,
        () -> specializationService.getMainMasteryById(anyLong()));
    verify(specializationRepository).findById(anyLong());
  }

  @Test
  void update_shouldReturnUpdatedSpecializationDto_whenSpecializationIsUpdated() {
    when(specializationRepository.findById(anyLong())).thenReturn(Optional.of(specialization));
    when(specializationRepository.existsSpecializationByUserIdAndName(anyLong(),
        anyString())).thenReturn(false);
    when(dataMapper.updateEntity(specializationDto, specialization)).thenReturn(specialization);
    when(specializationRepository.save(specialization)).thenReturn(specialization);
    when(dataMapper.toDto(specialization)).thenReturn(specializationDto);

    SpecializationDto result = specializationService.update(specializationDto);

    assertNotNull(result);
    assertEquals(specializationDto, result);
  }

  @Test
  void update_shouldThrowResourceAlreadyExistException_whenSpecializationNameAlreadyExists() {
    when(specializationRepository.findById(specId)).thenReturn(Optional.of(specialization));
    when(specializationRepository.existsSpecializationByUserIdAndName(anyLong(),
        anyString())).thenReturn(true);

    assertThrows(ResourceAlreadyExistException.class,
        () -> specializationService.update(specializationDto));
    verify(specializationRepository).findById(specId);
    verify(specializationRepository).existsSpecializationByUserIdAndName(anyLong(), anyString());
  }

  @Test
  void setAsMainById_shouldUpdateMainStatus() {
    specialization.setMain(false);
    Specialization mainSpecialization = new Specialization();
    mainSpecialization.setMain(true);

    when(specializationRepository.findById(anyLong())).thenReturn(Optional.of(specialization));
    when(specializationRepository.findSpecializationByUserIdAndMainTrue(anyLong()))
        .thenReturn(Optional.of(mainSpecialization));
    when(dataMapper.toDto(specialization)).thenReturn(specializationDto);

    SpecializationDto result = specializationService.setAsMainById(specId);

    assertNotNull(result);
    assertTrue(specialization.isMain());
    assertFalse(mainSpecialization.isMain());
  }

  @Test
  void deleteById_shouldCallRepositoryDelete() {
    doNothing().when(specializationRepository).deleteById(specId);
    specializationService.deleteById(specId);

    verify(specializationRepository).deleteById(specId);
  }

  @Test
  void getMasteriesBySpecializationId_shouldReturnListOfMasteries() {
    List<Mastery> listMasteries = new ArrayList<>();
    List<MasteryDto> listMasteriesDto = new ArrayList<>();
    listMasteriesDto.add(masteryDto);
    specialization.setMasteries(listMasteries);
    when(specializationRepository.findById(anyLong())).thenReturn(
        Optional.of(specialization));
    when(dataMapper.toDto(anyList())).thenReturn(listMasteriesDto);

    List<MasteryDto> result = specializationService.getMasteriesBySpecializationId(specId);

    assertNotNull(result);
    assertEquals(1, result.size());
  }

  @Test
  void createMasteriesForSpecialization_shouldCreateMasteries() {
    when(specializationRepository.findById(specId)).thenReturn(Optional.of(specialization));
    when(specializationRepository.save(specialization)).thenReturn(specialization);

    specializationService.createMasteriesForSpecialization(specId);

    assertNotNull(specialization.getMasteries());
    assertEquals(3, specialization.getMasteries().size());
  }

  @Test
  void setMainMasteryById_shouldUpdateMainMastery() {
    List<Mastery> listMasteries = new ArrayList<>();
    listMasteries.add(junMastery);
    listMasteries.add(midMastery);
    specialization.setMasteries(listMasteries);

    when(masteryService.getMasteryById(anyLong())).thenReturn(midMastery);
    when(specializationRepository.findById(specId)).thenReturn(Optional.of(specialization));
    when(dataMapper.toDto(midMastery)).thenReturn(masteryDto);

    MasteryDto result = specializationService.setMainMasteryById(specId, 2L);

    assertNotNull(result);
    assertEquals(midMastery, specialization.getMainMastery());
  }

}
