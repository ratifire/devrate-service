package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.entity.Education;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.EducationRepository;
import java.util.Optional;
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
public class EducationServiceTest {

  @Mock
  private EducationRepository educationRepository;

  @Mock
  private DataMapper<EducationDto, Education> mapper;

  @InjectMocks
  private EducationService educationService;

  private EducationDto educationDto;

  private Education education;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void before() {

    educationDto = EducationDto.builder()
        .id(1)
        .type("Course")
        .name("Hillel")
        .description("I learned a lot of knowledge")
        .startYear(2013)
        .endYear(2013)
        .build();

    education = Education.builder()
        .id(1)
        .type("Course")
        .name("Hillel")
        .description("I learned a lot of knowledge")
        .startYear(2013)
        .endYear(2013)
        .build();
  }

  @Test
  public void updateTest() {
    when(educationRepository.findById(anyLong())).thenReturn(Optional.ofNullable(education));
    when(educationRepository.save(any())).thenReturn(education);
    when(mapper.updateEntity(any(), any())).thenReturn(education);
    when(mapper.toDto(education)).thenReturn(educationDto);

    EducationDto result = educationService.update(anyLong(), educationDto);

    assertEquals(educationDto, result);
  }
}
