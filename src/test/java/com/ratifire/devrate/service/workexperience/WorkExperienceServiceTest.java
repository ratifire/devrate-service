package com.ratifire.devrate.service.workexperience;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.WorkExperienceDto;
import com.ratifire.devrate.entity.WorkExperience;
import com.ratifire.devrate.exception.WorkExperienceNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.WorkExperienceRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the EducationService class.
 */
@ExtendWith(MockitoExtension.class)
public class WorkExperienceServiceTest {

  @Mock
  private WorkExperienceRepository workExperienceRepository;

  @Mock
  private DataMapper<WorkExperienceDto, WorkExperience> mapper;

  @InjectMocks
  private WorkExperienceService workExperienceService;

  private WorkExperienceDto workExperienceDto;
  @Mock
  private WorkExperience workExperience;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void before() {
    workExperienceDto = WorkExperienceDto.builder()
        .id(1L)
        .startDate(LocalDate.ofEpochDay(2023 - 01 - 01))
        .endDate(LocalDate.ofEpochDay(2022 - 01 - 01))
        .position("Java Developer")
        .companyName("New Company")
        .description("Worked on various projects")
        .userId(8882)
        .responsibilityId("1, 2, 3")
        .build();
  }


  @Test
  public void getByIdTest() {
    List<WorkExperience> mockWorkExperiences = new ArrayList<>();
    mockWorkExperiences.add(workExperience);
    when(workExperienceRepository.findByUserId(anyLong())).thenReturn(mockWorkExperiences);

    List<WorkExperienceDto> result = workExperienceService.findByUserId(8882L);
    assertNotNull(result);

    List<WorkExperienceDto> expected = mockWorkExperiences.stream()
        .map(mapper::toDto)
        .collect(Collectors.toList());

    assertEquals(mockWorkExperiences.size(), result.size());
    assertEquals(expected, result);
  }

  @Test
  public void getByWithUnExpectedIdTest() {
    when(workExperienceRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());

    assertThrows(WorkExperienceNotFoundException.class, () -> {
      workExperienceService.findByUserId(8882L);
    });
  }

  @Test
  public void crateTest() {
    when(mapper.toEntity(any(WorkExperienceDto.class))).thenReturn(new WorkExperience());
    when(mapper.toDto(any(WorkExperience.class))).thenReturn(workExperienceDto);

    when(workExperienceRepository.save(any(WorkExperience.class))).thenAnswer(invocation -> {
      WorkExperience savedWorkExperience = invocation.getArgument(0);
      savedWorkExperience.setId(1L);
      return savedWorkExperience;
    });

    WorkExperienceDto result = workExperienceService.create(workExperienceDto);

    assertNotNull(result);
    assertEquals(workExperienceDto, result);
  }

  @Test
  public void updateTest() {
    when(mapper.toDto(any(WorkExperience.class))).thenReturn(workExperienceDto);
    when(workExperienceRepository.findById(anyLong())).thenReturn(
        Optional.of(new WorkExperience()));
    when(workExperienceRepository.save(any())).thenReturn(new WorkExperience());

    WorkExperienceDto result = workExperienceService.update(workExperienceDto);

    assertNotNull(result);
    assertEquals(workExperienceDto, result);
  }

  @Test
  public void updateWithUnExpectedIdTest() {
    when(workExperienceRepository.findById(anyLong())).thenThrow(
        WorkExperienceNotFoundException.class);

    assertThrows(WorkExperienceNotFoundException.class, () -> {
      workExperienceService.update(workExperienceDto);
    });
  }

  @Test
  public void deleteTest() {
    when(mapper.toEntity(any(WorkExperienceDto.class))).thenReturn(workExperience);
    when(mapper.toDto(any(WorkExperience.class))).thenReturn(workExperienceDto);
    when(workExperienceRepository.save(any(WorkExperience.class))).thenReturn(workExperience);

    WorkExperienceDto result = workExperienceService.create(workExperienceDto);

    assertNotNull(result);
    assertEquals(workExperienceDto, result);
  }
}